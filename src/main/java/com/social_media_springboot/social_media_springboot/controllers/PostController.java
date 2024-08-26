package com.social_media_springboot.social_media_springboot.controllers;

import com.social_media_springboot.social_media_springboot.DTO.CreatePostDTO;
import com.social_media_springboot.social_media_springboot.DTO.CreatePostResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.RequestPostDTO;
import com.social_media_springboot.social_media_springboot.DTO.UpdatePostDTO;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.mapper.PostMapper;
import com.social_media_springboot.social_media_springboot.services.PostService;
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
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping("/posts")
    public ResponseEntity<CreatePostResponseDTO> createPost(@Valid @RequestBody CreatePostDTO createPostDTO, @AuthenticationPrincipal User currentUser) {
        Post post = postService.createPost(createPostDTO, currentUser);

        return ResponseEntity.ok(postMapper.postToCreatePostResponseDTO(post));
    }

    @GetMapping("/posts")
    public ResponseEntity<List<RequestPostDTO>> queryPosts(
            @RequestParam("post_id") Optional<Long> postId,
            @RequestParam("title") Optional<String> title,
            @AuthenticationPrincipal User currentUser
    ) {
        //List<RequestPostDTO> result = postService.queryPosts(postId, title, currentUser);
        List<RequestPostDTO> result = postService.queryPosts(postId, title, currentUser).stream()
                .map(postMapper::postToRequestPostDTO)
                .toList();


        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<RequestPostDTO> getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser

    ) {
        RequestPostDTO postDTO = postMapper.postToRequestPostDTO(postService.getPostById(currentUser, id));
        return ResponseEntity.ok(postDTO);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<RequestPostDTO> updatePost(@PathVariable Long id, @AuthenticationPrincipal User currentUser, @Valid @RequestBody UpdatePostDTO post) {
        RequestPostDTO postDTO = postMapper.postToRequestPostDTO(postService.updatePostById(currentUser, id, post));

        return ResponseEntity.ok(postDTO);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id, @Valid @RequestBody UpdatePostDTO post, @AuthenticationPrincipal User currentUser) {
        postService.deletePost(currentUser, id);

        return ResponseEntity.ok("Post with id " + id + " successfully deleted");
    }

//    @PostMapping("/likes")
//    public ResponseEntity<String> likePost(
//            @RequestBody LikeDTO likeDTO,
//            @AuthenticationPrincipal User currentUser
//    ) {
//        postService.toggleLikePost(likeDTO, currentUser);
//
//        if (postService.isPostLikedByUser(likeDTO.getPostId(), currentUser)) {
//            return ResponseEntity.ok("You liked Post with id " + likeDTO.getPostId());
//        } else {
//            return ResponseEntity.ok("You unliked Post with id " + likeDTO.getPostId());
//        }
//    }

}
