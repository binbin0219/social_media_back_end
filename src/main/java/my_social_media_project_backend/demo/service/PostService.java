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
import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.dto.UserDTO;
import my_social_media_project_backend.demo.dto.request.SharePostRequest;
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
import my_social_media_project_backend.demo.mapper.PostAttachmentMapper;
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
    private final PostLikeService postLikeService;
    private final PostMapper postMapper;
    private final UserService userService;

    public PostService(
            PostRepository postRepository,
            PostStatisticsService postStatisticsService,
            R2StorageService r2StorageService,
            PostAttachmentRepository postAttachmentRepository,
            UserStatisticService userStatisticService,
            UserRepository userRepository,
            PostVisibilityAllowRepository postVisibilityAllowRepository,
            PostVisibilityDenyRepository postVisibilityDenyRepository,
            FriendshipService friendshipService,
            PostLikeService postLikeService,
            PostMapper postMapper,
            UserService userService
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
        this.postLikeService = postLikeService;
        this.postMapper = postMapper;
        this.userService = userService;
    }

    public PaginatedResponseDTO<PostDTO> getPosts(Pageable pageable, Long userId) {

        Page<PostDTO> postPage = postRepository.getPosts(userId, null, pageable)
            .map(post -> buildPostDto(post, userId));

        List<PostDTO> postDTOs = postPage.getContent();

        // Specification<Post> spec = PostSpecification.getPosts(userId, null);
        // Page<Post> postPage = postRepository.findAll(spec, pageable);

        // List<PostDTO> postDTOs = postPage.getContent()
        //     .stream()
        //     .map(post -> buildPostDto(post, userId))
        //     .toList();

        return new PaginatedResponseDTO<>(
            postDTOs,
            postPage.getTotalElements(),
            (int) pageable.getOffset(),
            pageable.getPageSize()
        );
    }

    public PostDTO getPostDetails(Long postId, Long currentUserId) {

        Post post = postRepository.getPostById(currentUserId, postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        return buildPostDto(post, currentUserId);
    }

    @Transactional
    public Long create(PostCreateDTO postCreateDTO, User user) throws BadRequestException {

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
        handlePostPrivacySetting(savedPost, postCreateDTO.getPrivacySetting(), postCreateDTO.getSelectedFriendIds());

        postStatisticsService.create(savedPost);

        userStatisticService.incrementPostCount(user.getId());

        return post.getId();
    }

    @Transactional
    public PostDTO editPost(Long postId, PostCreateDTO postCreateDTO, User user) throws BadRequestException {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("Post not found"));

        // Optional: ownership check
        if (!post.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You are not allowed to edit this post");
        }

        if(post.getSharedPost() == null && postCreateDTO.getContent().isEmpty()) {
            throw new BadRequestException("Content cannot be empty");
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

        handlePostPrivacySetting(updatedPost, postCreateDTO.getPrivacySetting(), postCreateDTO.getSelectedFriendIds());

        // ========================
        // Build response DTO
        // ========================
        return getPostDetails(post.getId(), user.getId());
    }

    @Transactional
    public Long sharePost(SharePostRequest dto, User user) throws BadRequestException {

        // 1. Resolve the original post — throws PostNotFoundException if absent
        Post originalPost = getPostByIdOrFail(dto.getOriginalPostId());

        // 2. Guard: don't allow sharing a post that is itself a repost of something else
        if (originalPost.getSharedPost() != null) {
            originalPost = originalPost.getSharedPost();
        }

        // prevent author from sharing their own post
        if (originalPost.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot share your own post");
        }

        // 3. Build the repost entity
        final Post repost = new Post();
        repost.setUser(user);
        repost.setSharedPost(originalPost);
        repost.setContent(dto.getContent());          // nullable caption
        repost.setPrivacySetting(dto.getPrivacySetting());
        repost.setCommentStatus(dto.getCommentStatus());
        repost.setIsSensitiveContent(Boolean.TRUE.equals(dto.getIsSensitive()) ? 1 : 0);

        final Post savedRepost = postRepository.save(repost);

        // Increment share count of the original post
        postStatisticsService.incrementShareCount(originalPost.getId());

        // 4. Handle WCV / WCNV allow-/deny-lists
        handlePostPrivacySetting(savedRepost, dto.getPrivacySetting(), dto.getSelectedFriendIds());

        // 5. Create statistics row and update the sharer's post count
        postStatisticsService.create(savedRepost);
        userStatisticService.incrementPostCount(user.getId());

        return savedRepost.getId();
    }

    @Transactional
    public void delete(Long postId) throws EntityNotFoundException, BadRequestException {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = getPostByIdOrFail(postId);

        if (!Objects.equals(post.getUser().getId(), userDetails.getUserId())) {
            throw new BadRequestException("Failed to delete post: user not author of the post");
        }

        if (post.getSharedPost() != null) {
            postStatisticsService.decrementShareCount(post.getSharedPost().getId());
        }

        PostStatistic postStatistic = postStatisticsService.getByPostId(postId);
        r2StorageService.deleteFolder(StoragePathUtils.getPostDirLinkOnR2(postId));
        postRepository.deleteById(postId);
        userStatisticService.decrementPostCount(post.getUser().getId());
        userStatisticService.decrementLikeCount(post.getUser().getId(), postStatistic.getLikeCount());
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

    private void handlePostPrivacySetting(
            Post post,
            PostPrivacySetting privacySetting,
            List<Long> selectedFriendIds
    ) throws BadRequestException {

        postVisibilityAllowRepository.deleteByPostId(post.getId());
        postVisibilityDenyRepository.deleteByPostId(post.getId());

        // Only handle WCV and WCNY
        if (privacySetting != PostPrivacySetting.WCV && privacySetting != PostPrivacySetting.WCNV) {
            return;
        }

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

    public boolean checkCanComment(Long userId, Post post) {
        CommentStatus commentStatus = post.getCommentStatus();

        if (commentStatus == CommentStatus.CLOSED) {
            return false;
        }

        if (commentStatus == CommentStatus.ONLY_FRIENDS && !Objects.equals(post.getUser().getId(), userId)) {
            return friendshipService.checkIsFriend(userId, post.getUser().getId());
        }

        return true;
    }

    public List<FriendDTO> resolveVisibility(Post post, Long currentUserId) {

        if (post.getPrivacySetting() == PostPrivacySetting.WCV) {

            return post.getVisibilityAllows()
                    .stream()
                    .map(v -> {
                        List<StoryDTO> stories = userService.buildUserDto(v.getUser(), currentUserId).getStories();
                        return FriendMapper.toDto(v.getUser(), stories);
                    })
                    .toList();
        }

        if (post.getPrivacySetting() == PostPrivacySetting.WCNV) {

            return post.getVisibilityDenys()
                    .stream()
                    .map(v -> {
                        List<StoryDTO> stories = userService.buildUserDto(v.getUser(), currentUserId).getStories();
                        return FriendMapper.toDto(v.getUser(), stories);
                    })
                    .toList();
        }

        return List.of();
    }

    private PostDTO buildPostDto(Post post, Long currentUserId) {

        boolean isLiked = postLikeService.isPostLikedByUser(post.getId(), currentUserId);

        Friendship friendship = friendshipService.findByUserAndFriendId(post.getUser().getId(),
            currentUserId
        );

        List<FriendDTO> visibility = resolveVisibility(post, currentUserId);

        boolean canComment = checkCanComment(currentUserId, post);

        Long likeCount = post.getPostStatistic() != null
            ? post.getPostStatistic().getLikeCount()
            : 0L;

        Long commentCount = post.getPostStatistic() != null
            ? post.getPostStatistic().getCommentCount()
            : 0L;

        Long shareCount = post.getPostStatistic() != null
            ? post.getPostStatistic().getShareCount()
            : 0L;

        List<PostAttachmentDTO> attachments = post.getAttachments()
            .stream()
            .map(att -> PostAttachmentMapper.toDto(post, att, r2StorageService))
            .toList();

        PostDTO sharedPostDTO = null;
        if (post.getSharedPost() != null) {
            sharedPostDTO = buildPostDto(post.getSharedPost(), currentUserId);
        }

        UserDTO userDTO = userService.buildUserDto(post.getUser(), currentUserId);

        return postMapper.toDto(
            post,
            visibility,
            canComment,
            isLiked,
            friendship,
            likeCount,
            commentCount,
            shareCount,
            attachments,
            sharedPostDTO,
            userDTO
        );
    }
}
