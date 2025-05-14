package my_social_media_project_backend.demo.projection;

import my_social_media_project_backend.demo.entity.ChatAttachment;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageView {
    String getId();
    UserSummary getSender();
    String getText();
    List<ChatAttachment> getAttachments();
    LocalDateTime getCreateAt();
}
