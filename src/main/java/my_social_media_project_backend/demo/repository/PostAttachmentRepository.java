package my_social_media_project_backend.demo.repository;

import my_social_media_project_backend.demo.entity.PostAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostAttachmentRepository extends JpaRepository<PostAttachment, String> {
    public List<PostAttachment> findByPost_IdIn(List<Long> postIds);
}
