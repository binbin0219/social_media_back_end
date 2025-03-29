package my_social_media_project_backend.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PostCreateDTO {

    @NotNull(message = "Content is required")
    @Size(max = 1000 , message = "Content cannot exceed 1000 letters")
    @Pattern(regexp = ".*\\S.*", message = "Title cannot be empty or whitespace only")
    private String content;

    @NotNull(message = "Title is required")
    @Size(max = 255 , message = "Title cannot exceed 255 letters")
    @Pattern(regexp = ".*\\S.*", message = "Title cannot be empty or whitespace only")
    private String title;

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }
}
