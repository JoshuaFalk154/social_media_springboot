package com.social_media_springboot.social_media_springboot.controllers;

import com.social_media_springboot.social_media_springboot.DTO.CreatePostDTO;
import com.social_media_springboot.social_media_springboot.DTO.CreatePostResponseDTO;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<CreatePostResponseDTO> createPost(@RequestBody CreatePostDTO createPostDTO, @AuthenticationPrincipal User currentUser) {
        CreatePostResponseDTO postResponseDTO = postService.createPost(createPostDTO, currentUser);

        return ResponseEntity.ok(postResponseDTO);

    }
}
