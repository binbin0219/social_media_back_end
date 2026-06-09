package my_social_media_project_backend.demo.mapper;

import my_social_media_project_backend.demo.dto.PostAttachmentDTO;
import my_social_media_project_backend.demo.entity.Post;
import my_social_media_project_backend.demo.entity.PostAttachment;
import my_social_media_project_backend.demo.service.R2StorageService;
import my_social_media_project_backend.demo.utility.FormatUtils;
import my_social_media_project_backend.demo.utility.StoragePathUtils;

public class PostAttachmentMapper {

    private PostAttachmentMapper() {
        // Prevent instantiation
    }

    public static PostAttachmentDTO toDto(
            Post post,
            PostAttachment attachment,
            R2StorageService r2StorageService
    ) {

        return new PostAttachmentDTO(
                attachment.getId(),
                r2StorageService.getPresignedUrl(
                        StoragePathUtils.getPostAttachmentLinkOnR2(
                                post.getId(),
                                attachment.getId(),
                                attachment.getFormat()
                        ),
                        FormatUtils.getContentTypeFromExtension(
                                attachment.getFormat()
                        )
                ),
                attachment.getFormat(),
                attachment.getMimeType()
        );
    }
}