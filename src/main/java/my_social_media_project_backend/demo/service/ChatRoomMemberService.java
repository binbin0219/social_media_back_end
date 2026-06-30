package my_social_media_project_backend.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import my_social_media_project_backend.demo.custom.CustomUserDetails;
import my_social_media_project_backend.demo.dto.ChatRoomMemberDTO;
import my_social_media_project_backend.demo.dto.StoryDTO;
import my_social_media_project_backend.demo.entity.ChatRoomMember;
import my_social_media_project_backend.demo.repository.ChatRoomMemberRepository;

@Service
public class ChatRoomMemberService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final StoryService storyService;

    public ChatRoomMemberService(ChatRoomMemberRepository chatRoomMemberRepository, StoryService storyService) {
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.storyService = storyService;
    }

    public List<ChatRoomMemberDTO> getChatRoomMemberDTOs (String chatRoomId, Integer start, Integer length) {
        int pageNumber = start / length;
        PageRequest pageable = PageRequest.of(pageNumber, length, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ChatRoomMember> postPage = chatRoomMemberRepository.findByChatRoomId(chatRoomId, pageable);
        Long currentUserId = getCurrentUserId();

        return postPage.getContent()
            .stream()
            .map(member -> buildDto(member, currentUserId))
            .toList();
    }

    public Map<String, List<ChatRoomMemberDTO>> getAllByPrivateChatRoomIds(List<String> privateChatRoomIds) {
        List<ChatRoomMember> chatRoomMemberDTOS = chatRoomMemberRepository.findAllByPrivateChatRoomIds(privateChatRoomIds);
        Long currentUserId = getCurrentUserId();

        return chatRoomMemberDTOS.stream()
            .map(member -> buildDto(member, currentUserId))
            .collect(Collectors.groupingBy(ChatRoomMemberDTO::getChatRoomId));
    }

    public void markAsRead(String chatRoomId, Long userId) {
        chatRoomMemberRepository.markAsRead(chatRoomId, userId, LocalDateTime.now());
    }

    public void incrementOtherMemberUnreadCount(String chatRoomId, Long senderId) {
        chatRoomMemberRepository.incrementOtherMemberUnreadCount(chatRoomId, senderId);
    }

    public ChatRoomMemberDTO buildDto(ChatRoomMember chatRoomMember, Long currentUserId) {
        List<StoryDTO> storyDTOs = storyService.getActiveStoryDTOsByUserId(chatRoomMember.getUser().getId(), currentUserId);

        return new ChatRoomMemberDTO(
            chatRoomMember.getId(),
            chatRoomMember.getChatRoom().getId(),
            chatRoomMember.getUser().getId(),
            chatRoomMember.getUser().getUsername(),
            storyDTOs,
            chatRoomMember.getUser().getUpdatedAt()
        );
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return null;
        }

        return userDetails.getUserId();
    }
}
