package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.PostCreatedResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostNestedDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostResponseDTO;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.services.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostMapper {

    private final LikeService likeService;
    private final LikeMapper likeMapper;
    private final UserMapper userMapper;

    public PostCreatedResponseDTO postToCreatePostResponseDTO(Post post) {
        return PostCreatedResponseDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public PostResponseDTO postToRequestPostDTO(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .isPublic(post.isPublic())
                //.owner(userToUserDTO(post.getOwner()))
                .owner(userMapper.userToNestedUserDTO(post.getOwner()))
                //.likes(post.getLikes().stream().map(like -> likeMapper.likeToRequestLikeDTO(like.getUser(), postToPostNestedDTO(like.getPost()))).toList())
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
