package my_social_media_project_backend.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.FriendDTO;
import my_social_media_project_backend.demo.dto.PaginatedResponseDTO;
import my_social_media_project_backend.demo.dto.PostAttachmentDTO;
import my_social_media_project_backend.demo.dto.PostCreateDTO;
import my_social_media_project_backend.demo.dto.PostDTO;
import my_social_media_project_backend.demo.entity.Friendship;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostAttachment;
import my_social_media_project_backend.demo.entity.PostStatistic;
import my_social_media_project_backend.demo.entity.PostVisibilityAllow;
import my_social_media_project_backend.demo.entity.PostVisibilityDeny;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.enums.CommentStatus;
import my_social_media_project_backend.demo.enums.PostPrivacySetting;
import my_social_media_project_backend.demo.exception.MaximumPostAttachmentException;
import my_social_media_project_backend.demo.exception.PostNotFoundException;
import my_social_media_project_backend.demo.mapper.FriendMapper;
import my_social_media_project_backend.demo.mapper.PostMapper;
import my_social_media_project_backend.demo.repository.PostAttachmentRepository;
import my_social_media_project_backend.demo.repository.PostRepository;
import my_social_media_project_backend.demo.repository.PostVisibilityAllowRepository;
import my_social_media_project_backend.demo.repository.PostVisibilityDenyRepository;
import my_social_media_project_backend.demo.repository.UserRepository;
import my_social_media_project_backend.demo.utility.ContentTypeUtils;
import my_social_media_project_backend.demo.utility.StoragePathUtils;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostStatisticsService postStatisticsService;
    private final R2StorageService r2StorageService;
    private final PostAttachmentRepository postAttachmentRepository;
    private final UserStatisticService userStatisticService;
    private final UserRepository userRepository;
    private final PostVisibilityAllowRepository postVisibilityAllowRepository;
    private final PostVisibilityDenyRepository postVisibilityDenyRepository;
    private final FriendshipService friendshipService;

    public PostService(
            PostRepository postRepository,
            PostStatisticsService postStatisticsService,
            R2StorageService r2StorageService,
            PostAttachmentRepository postAttachmentRepository,
            UserStatisticService userStatisticService,
            UserRepository userRepository,
            PostVisibilityAllowRepository postVisibilityAllowRepository,
            PostVisibilityDenyRepository postVisibilityDenyRepository,
            FriendshipService friendshipService
    ) {
        this.postRepository = postRepository;
        this.postStatisticsService = postStatisticsService;
        this.r2StorageService = r2StorageService;
        this.postAttachmentRepository = postAttachmentRepository;
        this.userStatisticService = userStatisticService;
        this.userRepository = userRepository;
        this.postVisibilityAllowRepository = postVisibilityAllowRepository;
        this.postVisibilityDenyRepository = postVisibilityDenyRepository;
        this.friendshipService = friendshipService;
    }

    public PaginatedResponseDTO<PostDTO> getPosts(Pageable pageable, Long userId) {

        Page<PostDTO> postPage = postRepository.getPosts(userId, null, pageable)
            .map(row -> {
                    Post post = (Post) row[0];
                    Friendship friendship = (Friendship) row[1];
                    boolean isLiked = (boolean) row[2];

                    return PostMapper.toDto(
                        post, 
                        resolveVisibility(post), 
                        checkCanComment(userId, post),
                        isLiked,
                        friendship,
                        userId,
                        r2StorageService
                    );
                }
            );

        return new PaginatedResponseDTO<>(
                postPage.getContent(),
                postPage.getTotalElements(),
                (int) pageable.getOffset(),
                pageable.getPageSize()
        );
    }

    public PostDTO getPostDetails(Long postId, Long currentUserId) {

        Object[] row = postRepository.getPostById(currentUserId, postId);

        Post post = (Post) row[0];
        Friendship friendship = (Friendship) row[1];
        boolean isLiked = (boolean) row[2];

        final List<FriendDTO> vilibilityList = resolveVisibility(post);

        return PostMapper.toDto(
                post,
                vilibilityList,
                checkCanComment(currentUserId, post),
                isLiked,
                friendship,
                currentUserId,
                r2StorageService
        );
    }

    @Transactional
    public PostDTO create(PostCreateDTO postCreateDTO, User user) throws BadRequestException {

        if (postCreateDTO.getAttachments().size() > 10) {
            throw new MaximumPostAttachmentException(
                    "Cannot have more than 10 attachments for a post"
            );
        }

        final Post post = new Post();

        post.setUser(user);
        post.setContent(postCreateDTO.getContent());
        post.setPrivacySetting(postCreateDTO.getPrivacySetting());
        post.setCommentStatus(postCreateDTO.getCommentStatus());
        post.setIsSensitiveContent(postCreateDTO.getIsSensitive() ? 1 : 0);

        // Attachments
        if (!postCreateDTO.getAttachments().isEmpty()) {

            List<PostAttachment> postAttachments
                    = postCreateDTO.getAttachments()
                            .stream()
                            .map(attachment -> {

                                PostAttachment postAttachment
                                        = new PostAttachment();

                                postAttachment.setFormat(
                                        ContentTypeUtils.getExtensionFromContentType(
                                                attachment.getContentType()
                                        )
                                );

                                postAttachment.setPost(post);

                                postAttachment.setMimeType(
                                        attachment.getContentType()
                                );

                                return postAttachment;
                            })
                            .toList();

            post.setAttachments(postAttachments);
        }

        final Post savedPost = postRepository.save(post);

        // Handle privacy logic
        handlePostPrivacySetting(savedPost, postCreateDTO);

        postStatisticsService.create(savedPost);

        userStatisticService.incrementPostCount(user.getId());

        return getPostDetails(post.getId(), user.getId());
    }

    @Transactional
    public PostDTO editPost(Long postId, PostCreateDTO postCreateDTO, User user) throws BadRequestException {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Optional: ownership check
        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to edit this post");
        }

        // ========================
        // Update basic fields
        // ========================
        post.setContent(postCreateDTO.getContent());
        post.setPrivacySetting(postCreateDTO.getPrivacySetting());
        post.setCommentStatus(postCreateDTO.getCommentStatus());
        post.setIsSensitiveContent(postCreateDTO.getIsSensitive() ? 1 : 0);

        // ========================
        // Attachments logic
        // ========================
        if (postCreateDTO.getAttachments() != null
                && !postCreateDTO.getAttachments().isEmpty()) {

            if (postCreateDTO.getAttachments().size() > 10) {
                throw new MaximumPostAttachmentException(
                        "Cannot have more than 10 attachments for a post"
                );
            }

            // full replacement → delete old attachments
            post.getAttachments().clear();

            List<PostAttachment> newAttachments
                    = postCreateDTO.getAttachments()
                            .stream()
                            .map(file -> {
                                PostAttachment attachment = new PostAttachment();

                                attachment.setPost(post);
                                attachment.setMimeType(file.getContentType());
                                attachment.setFormat(
                                        ContentTypeUtils.getExtensionFromContentType(
                                                file.getContentType()
                                        )
                                );

                                return attachment;
                            })
                            .toList();

            post.getAttachments().addAll(newAttachments);
        }

        // Save updated post
        Post updatedPost = postRepository.save(post);

        handlePostPrivacySetting(updatedPost, postCreateDTO);

        // ========================
        // Build response DTO
        // ========================
        return getPostDetails(post.getId(), user.getId());
    }

    public Post getPostByIdOrFail(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found, provided post id : " + postId));
    }

    public Long getPostUserId(Long postId) {
        return postRepository.getPostUserId(postId);
    }

    public List<Object[]> getPostAndUserByIdOrFail(Long postId, Long userId) {
        return postRepository.getPostAndUserById(postId, userId)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new EntityNotFoundException("Post or User not found"));
    }

    public List<PostDTO> getPostDTOsByUserId(Integer start, Integer length, Long userId, Long currentUserId) {
        int pageNumber = start / length;
        PageRequest pageable = PageRequest.of(pageNumber, length, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostDTO> postPage = postRepository.getPostDTOByUserId(userId, currentUserId, pageable);
        attachPostAttachments(postPage.getContent());
        return postPage.getContent();
    }

    public List<Post> getPostsByUserId(Integer start, Integer length) {
        int pageNumber = start / length;
        PageRequest pageable = PageRequest.of(pageNumber, length, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.getContent();
    }

    public List<? extends PostDTO> attachPostAttachments(List<? extends PostDTO> postDTOS) {
        List<Long> postIds = postDTOS.stream().map(PostDTO::getId).toList();

        List<PostAttachment> postAttachments = postAttachmentRepository.findByPost_IdIn(postIds);

        Map<Long, List<PostAttachmentDTO>> attachmentsByPostId = postAttachments.stream()
                .collect(Collectors.groupingBy(
                        pa -> pa.getPost().getId(),
                        Collectors.mapping(attachment -> {
                            return new PostAttachmentDTO(
                                    attachment.getId(),
                                    null,
                                    attachment.getFormat(),
                                    attachment.getMimeType()
                            );
                        }, Collectors.toList())
                ));

        for (PostDTO postDTO : postDTOS) {
            List<PostAttachmentDTO> attachments = attachmentsByPostId.getOrDefault(postDTO.getId(), new ArrayList<>());
            postDTO.setAttachments(attachments);
        }

        return postDTOS;
    }

    public void updatePost(Post post, String newContent) {
        post.setContent(newContent);
        postRepository.save(post);
    }

    public void delete(Long postId) throws EntityNotFoundException, BadRequestException {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long postUserId = getPostUserId(postId);
        if (postUserId == null) {
            throw new EntityNotFoundException("Failed to delete post: post not found");
        }

        if (!Objects.equals(postUserId, userDetails.getUserId())) {
            throw new BadRequestException("Failed to delete post: user not author of the post");
        }

        PostStatistic postStatistic = postStatisticsService.getByPostId(postId);
        r2StorageService.deleteFolder(StoragePathUtils.getPostDirLinkOnR2(postId));
        postRepository.deleteById(postId);
        userStatisticService.decrementPostCount(postUserId);
        userStatisticService.decrementLikeCount(postUserId, postStatistic.getLikeCount());
    }

    private void handlePostPrivacySetting(
            Post post,
            PostCreateDTO postCreateDTO
    ) throws BadRequestException {

        PostPrivacySetting privacySetting = postCreateDTO.getPrivacySetting();

        postVisibilityAllowRepository.deleteByPostId(post.getId());
        postVisibilityDenyRepository.deleteByPostId(post.getId());

        // Only handle WCV and WCNY
        if (privacySetting != PostPrivacySetting.WCV && privacySetting != PostPrivacySetting.WCNV) {
            return;
        }

        List<Long> selectedFriendIds = postCreateDTO.getSelectedFriendIds();

        // Remove duplicates
        List<Long> uniqueFriendIds
                = selectedFriendIds.stream()
                        .distinct()
                        .toList();

        List<User> selectedUsers
                = userRepository.findAllById(uniqueFriendIds);

        // WCV -> Allow list
        if (privacySetting == PostPrivacySetting.WCV) {

            List<PostVisibilityAllow> allows
                    = selectedUsers.stream()
                            .map(selectedUser -> {

                                PostVisibilityAllow allow
                                        = new PostVisibilityAllow();

                                allow.setPost(post);
                                allow.setUser(selectedUser);

                                return allow;
                            })
                            .toList();

            postVisibilityAllowRepository.saveAll(allows);
        }

        // WCNY -> Deny list
        if (privacySetting == PostPrivacySetting.WCNV) {

            List<PostVisibilityDeny> denies
                    = selectedUsers.stream()
                            .map(selectedUser -> {

                                PostVisibilityDeny deny
                                        = new PostVisibilityDeny();

                                deny.setPost(post);
                                deny.setUser(selectedUser);

                                return deny;
                            })
                            .toList();

            postVisibilityDenyRepository.saveAll(denies);
        }
    }

    private boolean checkCanComment(Long userId, Post post) {
        CommentStatus commentStatus = post.getCommentStatus();

        if (commentStatus == CommentStatus.CLOSED) {
            return false;
        }

        if (commentStatus == CommentStatus.ONLY_FRIENDS && !Objects.equals(post.getUser().getId(), userId)) {
            return friendshipService.checkIsFriend(userId, post.getUser().getId());
        }

        return true;
    }

    private static List<FriendDTO> resolveVisibility(Post post) {

        if (post.getPrivacySetting() == PostPrivacySetting.WCV) {

            return post.getVisibilityAllows()
                    .stream()
                    .map(v -> FriendMapper.toDto(v.getUser()))
                    .toList();
        }

        if (post.getPrivacySetting() == PostPrivacySetting.WCNV) {

            return post.getVisibilityDenys()
                    .stream()
                    .map(v -> FriendMapper.toDto(v.getUser()))
                    .toList();
        }

        return List.of();
    }
}
