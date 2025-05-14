package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.entity.ChatMessage;
import my_social_media_project_backend.demo.projection.ChatMessageView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    @Query("""
     SELECT cm
       FROM ChatMessage cm
       LEFT JOIN FETCH cm.attachments
      WHERE cm.chatRoom.id = :chatRoomId
    """)
    Page<ChatMessageView> findChatMessages(
            @Param("chatRoomId") String chatRoomId,
            Pageable pageable
    );
}
