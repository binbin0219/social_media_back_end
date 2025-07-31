package my_social_media_project_backend.demo.repository;

import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.dto.ChatRoomMemberDTO;
import my_social_media_project_backend.demo.entity.ChatRoomMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, String> {

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.ChatRoomMemberDTO(
            crm.id,
            crm.chatRoom.id,
            crm.user.id,
            crm.user.username,
            crm.user.updatedAt
        )
        FROM ChatRoomMember crm
        WHERE crm.chatRoom.id = :chatRoomId
    """)
    public Page<ChatRoomMemberDTO> findByChatRoomId(@Param("chatRoomId") String chatRoomId, Pageable pageable);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.ChatRoomMemberDTO(
            crm.id,
            crm.chatRoom.id,
            u.id,
            u.username,
            u.updatedAt
        )
        FROM ChatRoomMember crm
        JOIN crm.user u
        WHERE crm.chatRoom.id IN :chatRoomIds AND crm.chatRoom.type = PRIVATE
    """)
    public List<ChatRoomMemberDTO> findAllByPrivateChatRoomIds(@Param("chatRoomIds") List<String> privateChatRoomIds);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ChatRoomMember crm SET crm.lastSeenAt = :timestamp, crm.unreadCount = 0 WHERE crm.chatRoom.id = :chatRoomId AND crm.user.id = :userId")
    void markAsRead(@Param("chatRoomId") String chatRoomId, @Param("userId") Long userId, @Param("timestamp") LocalDateTime timestamp);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ChatRoomMember crm SET crm.unreadCount = crm.unreadCount + 1 WHERE crm.chatRoom.id = :chatRoomId AND crm.user.id != :senderId")
    void incrementOtherMemberUnreadCount(@Param("chatRoomId") String chatRoomId, @Param("senderId") Long senderId);
}
