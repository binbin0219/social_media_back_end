package my_social_media_project_backend.demo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import my_social_media_project_backend.demo.entity.Media;
import my_social_media_project_backend.demo.repository.MediaRepository;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;
    private final R2StorageService r2StorageService;

    public MediaService(MediaRepository mediaRepository,
                        R2StorageService r2StorageService) {
        this.mediaRepository = mediaRepository;
        this.r2StorageService = r2StorageService;
    }

    /**
     * Upload file to R2 + save DB record
     */
    public Media save(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        try {
            String extension = getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;

            // You can organize folder structure if needed:
            String path = "media/" + fileName;

            byte[] bytes = file.getBytes();

            String publicUrl = r2StorageService.uploadFile(path, bytes);

            Media media = new Media();
            media.setUrl(publicUrl);
            media.setMimeType(file.getContentType());
            media.setSize(file.getSize());
            media.setCreatedAt(LocalDateTime.now());

            return mediaRepository.save(media);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload media", e);
        }
    }

    /**
     * Get media by id
     */
    public Media get(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Media not found"));
    }

    /**
     * Delete media (DB + R2)
     */
    public void delete(Long id) {
        Media media = get(id);

        String key = extractKeyFromUrl(media.getUrl());

        r2StorageService.deleteFile(key);
        mediaRepository.delete(media);
    }

    /**
     * Replace media file
     */
    public Media replace(Long id, MultipartFile newFile) {
        Media existing = get(id);

        try {
            // delete old file
            String oldKey = extractKeyFromUrl(existing.getUrl());
            r2StorageService.deleteFile(oldKey);

            // upload new file
            String extension = getExtension(newFile.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;
            String path = "media/" + fileName;

            String newUrl = r2StorageService.uploadFile(path, newFile.getBytes());

            existing.setUrl(newUrl);
            existing.setMimeType(newFile.getContentType());
            existing.setSize(newFile.getSize());
            existing.setCreatedAt(LocalDateTime.now());

            return mediaRepository.save(existing);

        } catch (IOException e) {
            throw new RuntimeException("Failed to replace media", e);
        }
    }

    /**
     * Convert relative or full URL → public URL (same idea as .NET GetFullUrl)
     */
    public String getFullUrl(String url) {
        return url; // already public from R2 in your case
    }

    // ---------------- helpers ----------------

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * Extract R2 object key from full URL
     * Example:
     * https://cdn.xxx.com/media/abc.png → media/abc.png
     */
    private String extractKeyFromUrl(String url) {
        return url.substring(url.indexOf("/", 8)); // skip https://
    }
}