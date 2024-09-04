package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.PostCreatedResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostNestedDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostResponseDTO;
import com.social_media_springboot.social_media_springboot.entities.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class PostMapper {


    private final LikeMapper likeMapper;
    private final UserMapper userMapper;

    @Autowired
    public PostMapper(@Lazy LikeMapper likeMapper, @Lazy UserMapper userMapper) {
        this.likeMapper = likeMapper;
        this.userMapper = userMapper;
    }


    public PostCreatedResponseDTO postToCreatePostResponseDTO(Post post) {
        return PostCreatedResponseDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    // renamed
    public PostResponseDTO postToPostResponseDTO(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .isPublic(post.isPublic())
                .owner(userMapper.userToNestedUserDTO(post.getOwner()))
                .likes(Optional.ofNullable(post.getLikes())
                        .orElse(Collections.emptyList())
                        .stream().map(likeMapper::likeToLikeNestedDTO).toList()
                )
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }


    public PostNestedDTO postToPostNestedDTO(Post post) {
        return PostNestedDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .Id(post.getId())
                .build();
    }


}
