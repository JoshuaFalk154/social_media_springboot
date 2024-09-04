package com.social_media_springboot.social_media_springboot.controllers;

import com.social_media_springboot.social_media_springboot.DTO.PostCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostCreatedResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostUpdateDTO;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.mapper.PostMapper;
import com.social_media_springboot.social_media_springboot.services.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Post")
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;


    @PostMapping("/posts")
    public ResponseEntity<PostCreatedResponseDTO> createPost(@Valid @RequestBody PostCreateDTO postCreateDTO, @AuthenticationPrincipal User currentUser) {
        Post post = postService.createPost(postCreateDTO, currentUser);

        return ResponseEntity.ok(postMapper.postToCreatePostResponseDTO(post));
    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDTO>> queryPosts(
            @RequestParam("post_id") Optional<Long> postId,
            @RequestParam("title") Optional<String> title,
            @AuthenticationPrincipal User currentUser
    ) {
        List<PostResponseDTO> result = postService.queryPosts(postId, title, currentUser).stream()
                .map(postMapper::postToPostResponseDTO)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser

    ) {
        PostResponseDTO postDTO = postMapper.postToPostResponseDTO(postService.getPostById(currentUser, id));
        return ResponseEntity.ok(postDTO);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @AuthenticationPrincipal User currentUser, @Valid @RequestBody PostUpdateDTO post) {
        PostResponseDTO postDTO = postMapper.postToPostResponseDTO(postService.updatePostById(currentUser, id, post));

        return ResponseEntity.ok(postDTO);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        postService.deletePost(currentUser, id);

        return ResponseEntity.ok("Post with id " + id + " successfully deleted");
    }


}
