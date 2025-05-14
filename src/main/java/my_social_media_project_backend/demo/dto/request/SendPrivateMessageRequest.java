package my_social_media_project_backend.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendPrivateMessageRequest(
        @NotNull Long peerId,
        @NotBlank String text
) {

}
