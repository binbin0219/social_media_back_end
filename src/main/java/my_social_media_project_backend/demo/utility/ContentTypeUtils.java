package my_social_media_project_backend.demo.utility;

import java.util.Map;

public class ContentTypeUtils {

    public static String getExtensionFromContentType(String contentType) {
        Map<String, String> contentTypeToExtension = Map.ofEntries(
                // Images
                Map.entry("image/jpeg", "jpg"),
                Map.entry("image/png", "png"),
                Map.entry("image/gif", "gif"),
                Map.entry("image/webp", "webp"),
                Map.entry("image/bmp", "bmp"),
                Map.entry("image/tiff", "tiff"),
                Map.entry("image/svg+xml", "svg"),
                Map.entry("image/x-icon", "ico"),
                Map.entry("image/heif", "heif"),
                Map.entry("image/heic", "heic"),

                // Videos
                Map.entry("video/mp4", "mp4"),
                Map.entry("video/mpeg", "mpeg"),
                Map.entry("video/ogg", "ogv"),
                Map.entry("video/webm", "webm"),
                Map.entry("video/quicktime", "mov"),
                Map.entry("video/x-msvideo", "avi"),
                Map.entry("video/x-ms-wmv", "wmv"),
                Map.entry("video/x-flv", "flv"),
                Map.entry("video/3gpp", "3gp"),
                Map.entry("video/3gpp2", "3g2"),
                Map.entry("video/x-matroska", "mkv"),

                // Other (from your original list)
                Map.entry("application/pdf", "pdf"),
                Map.entry("text/plain", "txt"),
                Map.entry("application/json", "json")
        );

        return contentTypeToExtension.getOrDefault(contentType, "bin"); // fallback to "bin"
    }
}
