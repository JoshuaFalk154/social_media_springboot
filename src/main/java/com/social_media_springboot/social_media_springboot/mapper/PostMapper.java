package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.CreatePostResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostBasicDTO;
import com.social_media_springboot.social_media_springboot.DTO.RequestPostDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserDTO;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.services.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostMapper {

    private final LikeService likeService;
    private final LikeMapper likeMapper;

    public CreatePostResponseDTO postToCreatePostResponseDTO(Post post) {
        return CreatePostResponseDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public RequestPostDTO postToRequestPostDTO(Post post) {
        return RequestPostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .isPublic(post.isPublic())
                .owner(userToUserDTO(post.getOwner()))
                .likes(post.getLikes().stream().map(like -> likeMapper.likeToRequestLikeDTO(like.getUser(), postToPostBasicDTO(like.getPost()))).toList())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .build();
    }

    public PostBasicDTO postToPostBasicDTO(Post post) {
        return PostBasicDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .Id(post.getId())
                .build();
    }
}
