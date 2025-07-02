package my_social_media_project_backend.demo.dto;

public class PostAttachmentDTO {
    private String id;
    private String presignedUrl;
    private String format;
    private String mimeType;

    public PostAttachmentDTO(String id, String presignedUrl, String format, String mimeType) {
        this.id = id;
        this.presignedUrl = presignedUrl;
        this.format = format;
        this.mimeType = mimeType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPresignedUrl() {
        return presignedUrl;
    }

    public void setPresignedUrl(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
