package com.social_media_springboot.social_media_springboot.controllers;

import com.social_media_springboot.social_media_springboot.DTO.*;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.services.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<CreatePostResponseDTO> createPost(@RequestBody CreatePostDTO createPostDTO, @AuthenticationPrincipal User currentUser) {
        CreatePostResponseDTO postResponseDTO = postService.createPost(createPostDTO, currentUser);

        return ResponseEntity.ok(postResponseDTO);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<RequestPostDTO>> queryPosts(
            @RequestParam("post_id") Optional<Long> postId,
            @RequestParam("title") Optional<String> title,
            @AuthenticationPrincipal User currentUser
    ) {
        List<RequestPostDTO> result = postService.queryPosts(postId, title, currentUser);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<RequestPostDTO> getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser

    ) {
        RequestPostDTO postDTO = postService.getPostById(currentUser, id);
        return ResponseEntity.ok(postDTO);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<RequestPostDTO> updatePost(@PathVariable Long id, @AuthenticationPrincipal User currentUser, @RequestBody UpdatePostDTO post) {
        RequestPostDTO postDTO = postService.updatePostById(currentUser, id, post);

        return ResponseEntity.ok(postDTO);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id, @RequestBody UpdatePostDTO post, @AuthenticationPrincipal User currentUser) {
        postService.deletePost(currentUser, id);

        return ResponseEntity.ok("Post with id " + id + " successfully deleted");
    }

    @PostMapping("/likes")
    public ResponseEntity<String> likePost(
            @RequestBody LikeDTO likeDTO,
            @AuthenticationPrincipal User currentUser
            ) {
        postService.likePost(likeDTO, currentUser);



       return ResponseEntity.ok("You liked Post with id " + likeDTO.getPostId());
        //return ResponseEntity.ok("alles gut");
    }

}
