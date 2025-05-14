package my_social_media_project_backend.demo.dto;

import my_social_media_project_backend.demo.entity.ChatRoom;
import my_social_media_project_backend.demo.entity.ChatRoomMember;

import java.util.List;

public class PrivateChatDTO {
    private String id;
    private ChatRoom.Type type = ChatRoom.Type.PRIVATE;
    private boolean isTemp = false;
    private ChatRoomMember peer;
    private List<ChatMessageDTO> messages;
}
