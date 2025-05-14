package my_social_media_project_backend.demo.dto;

public class AllUnreadCountDTO {
    private String chatRoomId;
    private Long unreadCount;

    public AllUnreadCountDTO(String chatRoomId, Long unreadCount) {
        this.chatRoomId = chatRoomId;
        this.unreadCount = unreadCount;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
