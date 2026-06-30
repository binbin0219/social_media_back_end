package my_social_media_project_backend.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import my_social_media_project_backend.demo.entity.ChatRoomMember;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, String> {

    @Query("""
        SELECT crm
        FROM ChatRoomMember crm
        WHERE crm.chatRoom.id = :chatRoomId
    """)
    public Page<ChatRoomMember> findByChatRoomId(@Param("chatRoomId") String chatRoomId, Pageable pageable);

    @Query("""
        SELECT crm
        FROM ChatRoomMember crm
        JOIN crm.user u
        WHERE crm.chatRoom.id IN :chatRoomIds AND crm.chatRoom.type = PRIVATE
    """)
    public List<ChatRoomMember> findAllByPrivateChatRoomIds(@Param("chatRoomIds") List<String> privateChatRoomIds);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ChatRoomMember crm SET crm.lastSeenAt = :timestamp, crm.unreadCount = 0 WHERE crm.chatRoom.id = :chatRoomId AND crm.user.id = :userId")
    void markAsRead(@Param("chatRoomId") String chatRoomId, @Param("userId") Long userId, @Param("timestamp") LocalDateTime timestamp);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ChatRoomMember crm SET crm.unreadCount = crm.unreadCount + 1 WHERE crm.chatRoom.id = :chatRoomId AND crm.user.id != :senderId")
    void incrementOtherMemberUnreadCount(@Param("chatRoomId") String chatRoomId, @Param("senderId") Long senderId);
}
