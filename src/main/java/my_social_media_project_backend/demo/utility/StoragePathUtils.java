package my_social_media_project_backend.demo.utility;

public class StoragePathUtils {

    public static String getPostDirLinkOnR2(Long postId) {
        return String.format("post/%d", postId);
    }

    public static String getPostAttachmentLinkOnR2(Long postId, String attachmentId, String format) {
        return String.format(getPostDirLinkOnR2(postId) + "/attachments/%s/data.%s", attachmentId, format);
    }
}

