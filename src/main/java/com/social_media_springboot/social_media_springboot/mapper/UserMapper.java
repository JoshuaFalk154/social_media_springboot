package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.UserNestedDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserResponseDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserMapper {


    private final PostMapper postMapper;
    private final LikeMapper likeMapper;

    @Autowired
    public UserMapper(@Lazy PostMapper postMapper, @Lazy LikeMapper likeMapper) {
        this.postMapper = postMapper;
        this.likeMapper = likeMapper;
    }

    public UserNestedDTO userToNestedUserDTO(User user) {
        return UserNestedDTO.builder()
                .email(user.getEmail())
                .Id(user.getId())
                .build();
    }

    // TODO test
    public UserResponseDTO userToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .posts(Optional.ofNullable(user.getPosts())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(postMapper::postToPostNestedDTO)
                        .toList())
                .likes(Optional.ofNullable(user.getLikes())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(likeMapper::likeToLikeNestedDTO)
                        .toList())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
