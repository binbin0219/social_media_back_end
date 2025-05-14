package my_social_media_project_backend.demo.service;

import my_social_media_project_backend.demo.dto.ChatRoomMemberDTO;
import my_social_media_project_backend.demo.repository.ChatRoomMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatRoomMemberService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public ChatRoomMemberService(ChatRoomMemberRepository chatRoomMemberRepository) {
        this.chatRoomMemberRepository = chatRoomMemberRepository;
    }

    public List<ChatRoomMemberDTO> getChatRoomMemberDTOs (String chatRoomId, Integer offset, Integer recordPerPage) {
        int pageNumber = offset / recordPerPage;
        PageRequest pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<ChatRoomMemberDTO> postPage = chatRoomMemberRepository.findByChatRoomId(chatRoomId, pageable);
        return postPage.getContent();
    }

    public Map<String, List<ChatRoomMemberDTO>> getAllByPrivateChatRoomIds(List<String> privateChatRoomIds) {
        List<ChatRoomMemberDTO> chatRoomMemberDTOS = chatRoomMemberRepository.findAllByPrivateChatRoomIds(privateChatRoomIds);
        return chatRoomMemberDTOS.stream().collect(Collectors.groupingBy(ChatRoomMemberDTO::getChatRoomId));
    }

    public void markAsRead(String chatRoomId, Long userId) {
        chatRoomMemberRepository.markAsRead(chatRoomId, userId, LocalDateTime.now());
    }

    public void incrementOtherMemberUnreadCount(String chatRoomId, Long senderId) {
        chatRoomMemberRepository.incrementOtherMemberUnreadCount(chatRoomId, senderId);
    }
}
