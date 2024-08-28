package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.LikeResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostNestedDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeMapper {

    private final UserMapper userMapper;
    private final PostMapper postMapper;

    public LikeResponseDTO likeToRequestLikeDTO(User user, PostNestedDTO postNestedDTO) {
        return LikeResponseDTO.builder()
                .user(userMapper.userToNestedUserDTO(user))
                .post(postNestedDTO)
                .build();
    }

//    public LikeResponseDTO likeToRequestLikeDTO(Like like) {
//        return LikeResponseDTO.builder()
//                .user(userMapper.userToNestedUserDTO(like.getUser()))
//                .post(postMapper.postToPostNestedDTO(like.getPost()))
//                .build();
//    }


}
