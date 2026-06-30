package my_social_media_project_backend.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import my_social_media_project_backend.demo.dto.MediaDTO;
import my_social_media_project_backend.demo.entity.Media;
import my_social_media_project_backend.demo.mapper.MediaMapper;
import my_social_media_project_backend.demo.service.MediaService;

@RestController
@RequestMapping("api/media")
@Validated
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/")
    public ResponseEntity<MediaDTO> create(@RequestParam("file") MultipartFile file) {
        Media media = mediaService.save(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(MediaMapper.toDto(media));
    }
}
