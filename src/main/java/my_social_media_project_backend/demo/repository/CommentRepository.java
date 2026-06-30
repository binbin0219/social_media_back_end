package my_social_media_project_backend.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import my_social_media_project_backend.demo.dto.PostCommentDTO;
import my_social_media_project_backend.demo.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
        SELECT 
            c
        FROM Comment c
        WHERE (
            :postId IS NULL
            OR c.post.id = :postId
        )
        AND (
            :commentId IS NULL
            OR c.id = :commentId
        )
    """)
    Page<Comment> getComments(
        @Param("commentId") Long commentId,
        @Param("postId") Long postId,
        Pageable pageable
    );

    default Comment getCommentById(Long commentId) {
        List<Comment> result = getComments(
            commentId,
            null,
            Pageable.unpaged()
        ).getContent();

        return result.isEmpty() ? null : result.get(0);
    }

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.PostCommentDTO(
            c.id,
            c.content,
            new my_social_media_project_backend.demo.dto.PostCommentUserDTO(
                c.user.id,
                c.user.username,
                new my_social_media_project_backend.demo.dto.FriendshipDTO(
                    fs.userId,
                    fs.friendId,
                    fs.status,
                    CASE
                        WHEN fs.userId = :userId THEN true
                        ELSE false
                    END,
                    fs.createdAt
                ),
                null,
                c.user.updatedAt
            ),
            c.createdAt
        )
        FROM Comment c
        LEFT JOIN Friendship fs ON
            :userId IS NOT NULL 
            AND ((fs.userId = :userId AND fs.friendId = c.user.id) OR (fs.friendId = :userId AND fs.userId = c.user.id))
        WHERE c.post.id = :postId
    """)
    Page<PostCommentDTO> findPostComments(@Param("postId") Long postId, @Param("userId") Long userId, Pageable pageable);
}
