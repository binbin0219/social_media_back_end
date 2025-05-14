package my_social_media_project_backend.demo.dto;

import my_social_media_project_backend.demo.entity.ChatRoom;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomDTO {
    private String id;
    private String name;
    private ChatRoom.Type type;
    private String messagePreview;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
    private List<ChatRoomMemberDTO> members;
    private List<ChatMessageDTO> messages;
    private boolean isTemp = false;

    public ChatRoomDTO(String id, String name, ChatRoom.Type type, String messagePreview, LocalDateTime lastMessageAt, Long unreadCount, List<ChatRoomMemberDTO> members, List<ChatMessageDTO> messages) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.messagePreview = messagePreview;
        this.lastMessageAt = lastMessageAt;
        this.unreadCount = unreadCount;
        this.members = members;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChatRoom.Type getType() {
        return type;
    }

    public void setType(ChatRoom.Type type) {
        this.type = type;
    }

    public List<ChatRoomMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<ChatRoomMemberDTO> members) {
        this.members = members;
    }

    public List<ChatMessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageDTO> messages) {
        this.messages = messages;
    }

    public boolean getIsTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public String getMessagePreview() {
        return messagePreview;
    }

    public void setMessagePreview(String messagePreview) {
        this.messagePreview = messagePreview;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
