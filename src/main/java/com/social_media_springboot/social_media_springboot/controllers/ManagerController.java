package com.social_media_springboot.social_media_springboot.controllers;

import com.social_media_springboot.social_media_springboot.DTO.PostResponseDTO;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.mapper.PostMapper;
import com.social_media_springboot.social_media_springboot.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/moderator")
@RequiredArgsConstructor
public class ManagerController {

    private final PostService postService;
    private final PostMapper postMapper;

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);

        return ResponseEntity.ok(postMapper.postToPostResponseDTO(post));
    }
}
