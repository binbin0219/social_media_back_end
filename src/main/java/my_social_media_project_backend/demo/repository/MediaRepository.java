package my_social_media_project_backend.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import my_social_media_project_backend.demo.entity.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {
}