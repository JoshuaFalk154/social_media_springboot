package com.social_media_springboot.social_media_springboot.controllers;

import com.social_media_springboot.social_media_springboot.DTO.LikeDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final PostService postService;

    @PostMapping("/likes")
    public ResponseEntity<String> likePost(
            @RequestBody LikeDTO likeDTO,
            @AuthenticationPrincipal User currentUser
    ) {
        postService.toggleLikePost(likeDTO, currentUser);

        if (postService.isPostLikedByUser(likeDTO.getPostId(), currentUser)) {
            return ResponseEntity.ok("You liked Post with id " + likeDTO.getPostId());
        } else {
            return ResponseEntity.ok("You unliked Post with id " + likeDTO.getPostId());
        }
    }
}
