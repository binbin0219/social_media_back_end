package my_social_media_project_backend.demo.utility;

import java.util.Map;

public class FormatUtils {

    private static final Map<String, String> EXTENSION_TO_CONTENT_TYPE = Map.ofEntries(
            // Images
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("png", "image/png"),
            Map.entry("gif", "image/gif"),
            Map.entry("webp", "image/webp"),
            Map.entry("bmp", "image/bmp"),
            Map.entry("tiff", "image/tiff"),
            Map.entry("svg", "image/svg+xml"),
            Map.entry("ico", "image/x-icon"),
            Map.entry("heif", "image/heif"),
            Map.entry("heic", "image/heic"),

            // Videos
            Map.entry("mp4", "video/mp4"),
            Map.entry("mpeg", "video/mpeg"),
            Map.entry("ogv", "video/ogg"),
            Map.entry("webm", "video/webm"),
            Map.entry("mov", "video/quicktime"),
            Map.entry("avi", "video/x-msvideo"),
            Map.entry("wmv", "video/x-ms-wmv"),
            Map.entry("flv", "video/x-flv"),
            Map.entry("3gp", "video/3gpp"),
            Map.entry("3g2", "video/3gpp2"),
            Map.entry("mkv", "video/x-matroska"),

            // Other
            Map.entry("pdf", "application/pdf"),
            Map.entry("txt", "text/plain"),
            Map.entry("json", "application/json")
    );

    public static String getContentTypeFromExtension(String extension) {
        return EXTENSION_TO_CONTENT_TYPE.getOrDefault(extension.toLowerCase(), "application/octet-stream");
    }
}
