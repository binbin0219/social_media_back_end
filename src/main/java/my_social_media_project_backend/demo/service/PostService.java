package my_social_media_project_backend.demo.service;

import jakarta.persistence.EntityNotFoundException;
import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.*;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostAttachment;
import my_social_media_project_backend.demo.entity.PostStatistic;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.exception.MaximumPostAttachmentException;
import my_social_media_project_backend.demo.exception.PostNotFoundException;
import my_social_media_project_backend.demo.repository.PostAttachmentRepository;
import my_social_media_project_backend.demo.repository.PostRepository;
import my_social_media_project_backend.demo.utility.ContentTypeUtils;
import my_social_media_project_backend.demo.utility.FormatUtils;
import my_social_media_project_backend.demo.utility.StoragePathUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostStatisticsService postStatisticsService;
    private final R2StorageService r2StorageService;
    private final PostAttachmentRepository postAttachmentRepository;
    private final UserStatisticService userStatisticService;

    public PostService(PostRepository postRepository, PostStatisticsService postStatisticsService, R2StorageService r2StorageService, PostAttachmentRepository postAttachmentRepository, UserStatisticService userStatisticService) {
        this.postRepository = postRepository;
        this.postStatisticsService = postStatisticsService;
        this.r2StorageService = r2StorageService;
        this.postAttachmentRepository = postAttachmentRepository;
        this.userStatisticService = userStatisticService;
    }

    public PostWithUserIdDTO create(PostCreateDTO postCreateDTO, User user) {
        if(postCreateDTO.getAttachments().size() > 10) {
            throw new MaximumPostAttachmentException("Cannot have more than 10 attachments for a post");
        }

        final Post post = new Post();
        post.setUser(user);
        post.setContent(postCreateDTO.getContent());
        post.setTitle(postCreateDTO.getTitle());

        if(!postCreateDTO.getAttachments().isEmpty()) {
            List<PostAttachment> postAttachments = postCreateDTO.getAttachments().stream().map(attachment -> {
                PostAttachment postAttachment = new PostAttachment();
                postAttachment.setFormat(ContentTypeUtils.getExtensionFromContentType(attachment.getContentType()));
                postAttachment.setPost(post);
                postAttachment.setMimeType(attachment.getContentType());
                return postAttachment;
            }).toList();
            post.setAttachments(postAttachments);
        }

        final Post finalPost = postRepository.save(post);

        postStatisticsService.create(post);
        userStatisticService.incrementPostCount(user.getId());
        return new PostWithUserIdDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                0,
                0,
                false,
                post.getCreateAt(),
                user.getId(),
                post.getAttachments().stream().map(attachment -> {
                    return new PostAttachmentDTO(
                            attachment.getId(),
                            r2StorageService.getPresignedUrl(
                                    StoragePathUtils.getPostAttachmentLinkOnR2(finalPost.getId(), attachment.getId(), attachment.getFormat()),
                                    FormatUtils.getContentTypeFromExtension(attachment.getFormat())
                            ),
                            attachment.getFormat(),
                            attachment.getMimeType()
                    );
                }).toList()
        );
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

    public List<PostWithUserIdDTO> getPostDTOsByUserId(Integer offset, Integer recordPerPage, Long userId, Long currentUserId) {
        int pageNumber = offset / recordPerPage;
        PageRequest pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<PostWithUserIdDTO> postPage = postRepository.getPostDTOByUserId(userId, currentUserId, pageable);
        attachPostAttachments(postPage.getContent());
        return postPage.getContent();
    }

    public List<PostWithUserDTO> getPostWithUserDTOs(Integer offset, Integer recordPerPage, Long userId) {
        int pageNumber = offset / recordPerPage;
        PageRequest pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<PostWithUserDTO> postPage = postRepository.getPostWithUserDTO(userId, pageable);
        List<PostWithUserDTO> postWithUserDTOS = (List<PostWithUserDTO>) attachPostAttachments(postPage.getContent());
        return postPage.getContent();
    }

    public List<Post> getPostsByUserId(Integer offset, Integer recordPerPage) {
        int pageNumber = offset / recordPerPage;
        PageRequest pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
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


    public void updatePost(Post post, String newTitle, String newContent) {
        post.setContent(newContent);
        post.setTitle(newTitle);
        postRepository.save(post);
    }

    public void delete(Long postId) throws EntityNotFoundException, BadRequestException {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long postUserId = getPostUserId(postId);
        if(postUserId == null) {
            throw new EntityNotFoundException("Failed to delete post: post not found");
        }

        if(!Objects.equals(postUserId, userDetails.getUserId())) {
            throw new BadRequestException("Failed to delete post: user not author of the post");
        }

        PostStatistic postStatistic = postStatisticsService.getByPostId(postId);
        r2StorageService.deleteFolder(StoragePathUtils.getPostDirLinkOnR2(postId));
        postRepository.deleteById(postId);
        userStatisticService.decrementPostCount(postUserId);
        userStatisticService.decrementLikeCount(postUserId, postStatistic.getLikeCount());
    }
}
