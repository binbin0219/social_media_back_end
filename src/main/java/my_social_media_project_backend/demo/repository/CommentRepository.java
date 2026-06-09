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
    // @Query("""
    //     SELECT 
    //         c,
    //         CASE 
    //             WHEN p.commentStatus = OPEN THEN TRUE
    //             WHEN p.commentStatus = CLOSED THEN FALSE
    //             WHEN 
    //                 p.commentStatus = ONLY_FRIENDS 
    //                 AND EXISTS (
    //                     SELECT 1
    //                     FROM Friendship fs
    //                     WHERE ((fs.userId = :userId AND fs.friendId = p.user.id) OR (fs.friendId = :userId AND fs.userId = p.user.id))
    //                     AND fs.status = ACCEPTED
    //                 )
    //             THEN TRUE 
    //             ELSE FALSE
    //         END AS canComment
    //     FROM Comment c
    //     JOIN Post p ON p.id = c.post.id
    //     WHERE (
    //         :postIds IS NULL
    //         OR c.post.id IN :postIds
    //     )
    // """)
    // Page<Comment> getComments(
    //     @Param("postIds") List<Long> postIds,
    //     @Param("userId") Long userId,
    //     Pageable pageable
    // );

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.PostCommentDTO(
            c.id,
            c.content,
            new my_social_media_project_backend.demo.dto.PostCommentUserDTO(
                c.user.id,
                c.user.username,
                c.user.updatedAt
            ),
            c.createdAt
        )
        FROM Comment c
        WHERE c.post.id = :postId
    """)
    Page<PostCommentDTO> findPostComments(@Param("postId") Long postId, Pageable pageable);
}
