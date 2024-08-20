package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.PostBasicDTO;
import com.social_media_springboot.social_media_springboot.DTO.RequestLikeDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeMapper {

    private final UserMapper userMapper;

    public RequestLikeDTO likeToRequestLikeDTO(User user , PostBasicDTO postBasicDTO) {
        return RequestLikeDTO.builder()
                .user(userMapper.userToUserBasicDTO(user))
                .post(postBasicDTO)
                .build();
    }
}
