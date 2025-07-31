package my_social_media_project_backend.demo.service;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.UserSessionRegistry;
import my_social_media_project_backend.demo.dto.AllUnreadCountDTO;
import my_social_media_project_backend.demo.dto.ChatMessageDTO;
import my_social_media_project_backend.demo.dto.ChatRoomDTO;
import my_social_media_project_backend.demo.dto.ChatRoomMemberDTO;
import my_social_media_project_backend.demo.dto.response.SendPrivateMessageResponse;
import my_social_media_project_backend.demo.entity.ChatMessage;
import my_social_media_project_backend.demo.entity.ChatRoom;
import my_social_media_project_backend.demo.entity.ChatRoomMember;
import my_social_media_project_backend.demo.entity.User;
import my_social_media_project_backend.demo.eventListener.WebSocketEventListener;
import my_social_media_project_backend.demo.projection.UserSummary;
import my_social_media_project_backend.demo.repository.ChatMessageRepository;
import my_social_media_project_backend.demo.repository.ChatRoomRepository;
import my_social_media_project_backend.demo.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomMemberService chatRoomMemberService;
    private final ChatMessageService chatMessageService;
    private final UserSessionRegistry userSessionRegistry;
    private static final int PREVIEW_MAX_LENGTH = 10;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserRepository userRepository, ChatMessageRepository chatMessageRepository, ChatRoomMemberService chatRoomMemberService, ChatMessageService chatMessageService, UserSessionRegistry userSessionRegistry) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomMemberService = chatRoomMemberService;
        this.chatMessageService = chatMessageService;
        this.userSessionRegistry = userSessionRegistry;
    }

    public List<ChatRoomDTO> getChatRooms(Long userId, Integer offset, Integer recordPerPage) {
        int pageNumber = offset / recordPerPage;
        PageRequest pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        List<ChatRoomDTO> chatRoomDTOS = chatRoomRepository.findLimitedByUserId(userId, pageable).getContent();
        List<String> privateChatRoomIds = extractPrivateChatRoomIds(chatRoomDTOS);
        Map<String, List<ChatRoomMemberDTO>> chatRoomMemberDTOS = chatRoomMemberService.getAllByPrivateChatRoomIds(privateChatRoomIds);

        for (ChatRoomDTO chatRoomDTO : chatRoomDTOS) {
            if (ChatRoom.Type.PRIVATE.equals(chatRoomDTO.getType())) {
                List<ChatRoomMemberDTO> members = chatRoomMemberDTOS.get(chatRoomDTO.getId());
                chatRoomDTO.setMembers(members);
            }
        }

        return chatRoomDTOS;
    }

    public ChatRoomDTO getPrivateRoom(Long meId, Long peerId) {
        ChatRoomDTO chatRoom = chatRoomRepository.findPrivateRoomDTO(meId, peerId).orElse(null);

        if(chatRoom == null) {
            chatRoom = newTempPrivateChatRoom(meId, peerId);
            chatRoom.setTemp(true);
        } else {
            List<ChatRoomMemberDTO> chatRoomMemberDTOS = chatRoomMemberService.getChatRoomMemberDTOs(chatRoom.getId(), 0, 2);
            List<ChatMessageDTO> chatMessageDTOS = chatMessageService.getChatMessageDTOs(chatRoom.getId(), 0, 10);
            chatRoom.setMembers(chatRoomMemberDTOS);
            chatRoom.setMessages(chatMessageDTOS);
            chatRoomMemberService.markAsRead(chatRoom.getId(), meId);
        }

        return chatRoom;
    }

    public List<AllUnreadCountDTO> getAllUnreadCount(Long userId) {
        return chatRoomRepository.findAllUnreadCount(userId);
    }

    public SendPrivateMessageResponse sendPrivateMessage(Long meId, Long peerId, String text) {
        User meRef = userRepository.findById(meId).orElseThrow(() -> new EntityNotFoundException("user not found"));
        ChatRoom room = chatRoomRepository.findPrivateRoom(meId, peerId)
                .orElseThrow(() -> new EntityNotFoundException("Failed to send private message: private room not found, try initiate it first"));

        room.setMessagePreview(generatePreview(text));
        room.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(room);

        ChatMessage chatMessage = chatMessageRepository.save(newMessage(text, meRef, room));

        if(
                !userSessionRegistry.isConnected(peerId) ||
                !userSessionRegistry.isChatOpen(peerId) ||
                !userSessionRegistry.isViewingChat(peerId, room.getId())
        ) {
            chatRoomMemberService.incrementOtherMemberUnreadCount(room.getId(), meId);
        }

        return new SendPrivateMessageResponse(
                room.getId(),
                new ChatMessageDTO(
                        chatMessage.getId(),
                        meId,
                        meRef.getUsername(),
                        text,
                        new ArrayList<>(),
                        chatMessage.getCreateAt()
                ),
                room.getMessagePreview(),
                room.getLastMessageAt()
        );
    }

    public ChatRoomDTO initPrivateChatRoom(Long meId, Long peerId, String text) {
        ChatRoom room = chatRoomRepository.findPrivateRoom(meId, peerId).orElse(null);
        if(room != null) {
            throw new EntityExistsException("Failed to initiate private chat room: chat room already exist");
        }

        User meRef = userRepository.findById(meId).orElseThrow(() -> new EntityNotFoundException("Failed to init private chat: me doesn't exist !"));;
        User peerRef = userRepository.findById(peerId).orElseThrow(() -> new EntityNotFoundException("Failed to init private chat: peer doesn't exist !"));
        room = newPrivateRoom(meRef, peerRef);
        ChatMessage chatMessage = newMessage(text, meRef, room);

        room.setMessages(List.of(chatMessage));
        room.setMessagePreview(generatePreview(text));
        room.setLastMessageAt(chatMessage.getCreateAt());
        chatRoomRepository.save(room);

        ChatRoom currentRoom = room;
        return new ChatRoomDTO(
                room.getId(),
                room.getName(),
                room.getType(),
                room.getMessagePreview(),
                room.getLastMessageAt(),
                0L,
                room.getMembers().stream().map(member -> new ChatRoomMemberDTO(
                        member.getId(),
                        currentRoom.getId(),
                        member.getUser().getId(),
                        member.getUser().getUsername(),
                        member.getUser().getUpdatedAt()
                )).toList(),
                room.getMessages().stream().map(message -> new ChatMessageDTO(
                        message.getId(),
                        meId,
                        message.getSender().getUsername(),
                        text,
                        new ArrayList<>(),
                        message.getCreateAt()
                )).toList()
        );
    }

    private ChatMessage newMessage(String text, User sender, ChatRoom room) {
        ChatMessage message = new ChatMessage();
        message.setText(text);
        message.setSender(sender);
        message.setChatRoom(room);
        return message;
    }

    private ChatRoomDTO newTempPrivateChatRoom(Long userId1, Long userId2) {
        String tempChatRoomId = UlidCreator.getUlid().toString();
        List<UserSummary> userSummaries = userRepository.findUserSummariesByIds(List.of(userId1, userId2));
        List<ChatRoomMemberDTO> chatRoomMemberDTOS = userSummaries.stream()
                .map(userSummary -> new ChatRoomMemberDTO(
                        UlidCreator.getUlid().toString(),
                        tempChatRoomId,
                        userSummary.getId(),
                        userSummary.getUsername(),
                        userSummary.getUpdatedAt()
                )).toList();

        return new ChatRoomDTO(
                tempChatRoomId,
                null,
                ChatRoom.Type.PRIVATE,
                null,
                null,
                0L,
                chatRoomMemberDTOS,
                new ArrayList<>()
        );
    }

    private ChatRoom newPrivateRoom(User me, User peer) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setType(ChatRoom.Type.PRIVATE);

        List<ChatRoomMember> members = Stream.of(me, peer)
                .map(member -> {
                    ChatRoomMember chatRoomMember = new ChatRoomMember();
                    chatRoomMember.setChatRoom(chatRoom);
                    chatRoomMember.setUser(member);
                    if(Objects.equals(member.getId(), peer.getId())) {
                        chatRoomMember.setUnreadCount(1L);
                    }
                    return chatRoomMember;
                }).toList();

        chatRoom.setMembers(members);
        return chatRoom;
    }

    private ChatRoom newPrivateRoom(Long meId, Long peerId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setType(ChatRoom.Type.PRIVATE);

        List<User> memberRefs = List.of(
                userRepository.getReferenceById(meId),
                userRepository.getReferenceById(peerId)
        );
        List<ChatRoomMember> members = memberRefs.stream()
                .map(memberRef -> {
                    ChatRoomMember chatRoomMember = new ChatRoomMember();
                    chatRoomMember.setChatRoom(chatRoom);
                    chatRoomMember.setUser(memberRef);
                    return chatRoomMember;
                }).toList();

        chatRoom.setMembers(members);
        return chatRoomRepository.save(chatRoom);
    }

    public String generatePreview(String fullText) {
        if (fullText.length() <= PREVIEW_MAX_LENGTH) {
            return fullText;
        }
        return fullText.substring(0, PREVIEW_MAX_LENGTH) + "...";
    }

    private List<String> extractPrivateChatRoomIds(List<ChatRoomDTO> chatRoomDTOS) {
        return chatRoomDTOS.stream()
                .filter(dto -> ChatRoom.Type.PRIVATE.equals(dto.getType()))
                .map(ChatRoomDTO::getId)
                .toList();
    }
}
