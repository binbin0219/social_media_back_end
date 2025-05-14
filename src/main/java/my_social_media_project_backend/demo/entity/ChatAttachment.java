package my_social_media_project_backend.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;

@Entity
@Table(name = "chat_attachments")
public class ChatAttachment {
    @Id
    private String id;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UlidCreator.getUlid().toString();
        }
    }

    private String link;

    @ManyToOne
    @JoinColumn(name = "chat_message_id", nullable = false)
    @JsonIgnore
    private ChatMessage message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }
}
