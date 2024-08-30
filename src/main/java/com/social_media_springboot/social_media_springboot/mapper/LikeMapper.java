package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.LikeNestedDTO;
import com.social_media_springboot.social_media_springboot.DTO.LikeResponseDTO;
import com.social_media_springboot.social_media_springboot.entities.Like;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class LikeMapper {

    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @Autowired
    public LikeMapper(@Lazy UserMapper userMapper, @Lazy PostMapper postMapper) {
        this.userMapper = userMapper;
        this.postMapper = postMapper;
    }


    public LikeResponseDTO likeToRequestLikeDTO(Like like) {
        return LikeResponseDTO.builder()
                .user(userMapper.userToNestedUserDTO(like.getUser()))
                .post(postMapper.postToPostNestedDTO(like.getPost()))
                .build();
    }

    public LikeNestedDTO likeToLikeNestedDTO(Like like) {
        return LikeNestedDTO.builder()
                .ownerId(like.getUser().getId())
                .postId(like.getPost().getId())
                .build();
    }


}
