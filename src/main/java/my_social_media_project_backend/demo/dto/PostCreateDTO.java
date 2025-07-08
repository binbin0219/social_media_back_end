package my_social_media_project_backend.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class PostCreateDTO {

    @NotNull(message = "Content is required")
    @Size(max = 2500 , message = "Content cannot exceed 2500 letters")
    @Pattern(
            regexp = "(?s).*\\S.*",
            message = "Content cannot be empty or whitespace only"
    )
    private String content;

    @NotNull(message = "Title is required")
    @Size(max = 255 , message = "Title cannot exceed 255 letters")
    @Pattern(regexp = ".*\\S.*", message = "Title cannot be empty or whitespace only")
    private String title;

    private List<MultipartFile> attachments  = new ArrayList<>();

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<MultipartFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MultipartFile> attachments) {
        this.attachments = attachments;
    }
}
