package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.dto.PostCommentDTO;
import my_social_media_project_backend.demo.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
//    @Query("SELECT new my_social_media_project_backend.demo.dto.PostCommentDTO(c.id, c.content, c.user, c.create_at) " +
//            "FROM Comment c WHERE c.post.id = :post_id ORDER BY c.create_at DESC")
//    List<PostCommentDTO> findLimitedPostComments(
//            @Param("post_id") Long postId,
//            Pageable pageable
//        );

    @Query("""
        SELECT new my_social_media_project_backend.demo.dto.PostCommentDTO(
            c.id,
            c.content,
            new my_social_media_project_backend.demo.dto.PostCommentUserDTO(
                c.user.id,
                c.user.username,
                c.user.updatedAt
            ),
            c.createAt
        )
        FROM Comment c
        WHERE c.post.id = :postId
    """)
    Page<PostCommentDTO> findPostComments(@Param("postId") Long postId, Pageable pageable);
}
