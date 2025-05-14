package my_social_media_project_backend.demo.service;

import my_social_media_project_backend.demo.dto.ChatMessageDTO;
import my_social_media_project_backend.demo.projection.ChatMessageView;
import my_social_media_project_backend.demo.repository.ChatMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatMessageDTO> getChatMessageDTOs (String chatRoomId, Integer offset, Integer recordPerPage) {
        int pageNumber = offset / recordPerPage;
        PageRequest pageable = PageRequest.of(pageNumber, recordPerPage, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<ChatMessageView> chatMessagePage = chatMessageRepository.findChatMessages(chatRoomId, pageable);
        return chatMessagePage.getContent().reversed().stream().map(chatMessageView -> {
            return new ChatMessageDTO(
                    chatMessageView.getId(),
                    chatMessageView.getSender().getId(),
                    chatMessageView.getSender().getUsername(),
                    chatMessageView.getText(),
                    chatMessageView.getAttachments(),
                    chatMessageView.getCreateAt()
            );
        }).toList();
    }
}
