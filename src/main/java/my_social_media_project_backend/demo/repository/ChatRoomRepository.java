package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.dto.AllUnreadCountDTO;
import my_social_media_project_backend.demo.dto.ChatRoomDTO;
import my_social_media_project_backend.demo.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.ChatRoomDTO(
            crm.chatRoom.id,
            crm.chatRoom.name,
            crm.chatRoom.type,
            crm.chatRoom.messagePreview,
            crm.chatRoom.lastMessageAt,
            crm.unreadCount,
            null,
            null
        )
        FROM ChatRoomMember crm
        WHERE crm.user.id = :userId
    """)
    Page<ChatRoomDTO> findLimitedByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.ChatRoomDTO(
            cr.id,
            cr.name,
            cr.type,
            cr.messagePreview,
            cr.lastMessageAt,
            m.unreadCount,
            null,
            null
        )
        FROM ChatRoom cr
        JOIN cr.members m ON m.user.id = :meId
        WHERE cr.type = PRIVATE
          AND EXISTS (
              SELECT 1
              FROM ChatRoomMember cm
              WHERE cm.chatRoom.id = cr.id AND cm.user.id = :peerId
          )
    """)
    Optional<ChatRoomDTO> findPrivateRoomDTO(@Param("meId") Long meId, @Param("peerId") Long peerId);

    @Query("""
        SELECT cr
        FROM ChatRoom cr
        JOIN cr.members m
        WHERE cr.type = PRIVATE
            AND m.user.id IN (:userId1, :userId2)
        GROUP BY cr.id
        HAVING COUNT(DISTINCT m.user.id) = 2
    """)
    public Optional<ChatRoom> findPrivateRoom(@Param("userId1") Long userId1, @Param("userId2") Long userId2);



    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.AllUnreadCountDTO(
            cr.id,
            crm.unreadCount
        )
        FROM ChatRoomMember crm
        JOIN crm.chatRoom cr
        WHERE crm.user.id = :userId
    """)
    List<AllUnreadCountDTO> findAllUnreadCount(@Param("userId") Long userId);

}
