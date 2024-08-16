package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.CreatePostDTO;
import com.social_media_springboot.social_media_springboot.DTO.CreatePostResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostDTO;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public CreatePostResponseDTO createPost(CreatePostDTO createPostDTO, User currentUser) {
        Post createPost = Post.builder()
                .title(createPostDTO.getTitle())
                .content(createPostDTO.getContent())
                .isPublic(createPostDTO.isPublic())
                .owner(currentUser)
                .build();


        return postToCreatePostResponseDTO(postRepository.save(createPost));


    }

    public CreatePostResponseDTO postToCreatePostResponseDTO(Post dto) {
        return CreatePostResponseDTO.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .id(dto.getId())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
