package my_social_media_project_backend.demo.controller;

import my_social_media_project_backend.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/test")
public class TestController {

    @Autowired
    private PostRepository postRepository;

//    @GetMapping("/post/{id}")
//    public ResponseEntity<Test2DTO> getPost(@PathVariable Integer id) {
//        return postRepository.test(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
}
