package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.LikeDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostBasicDTO;
import com.social_media_springboot.social_media_springboot.DTO.RequestLikeDTO;
import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.repositories.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final AuthenticationService authenticationService;
    private final LikeRepository likeRepository;

    //private final PostService postService;


    public RequestLikeDTO likeToRequestLikeDTO(User user , PostBasicDTO postBasicDTO) {
        return RequestLikeDTO.builder()
                .userBasicDTO(authenticationService.userToUserBasicDTO(user))
                .postBasicDTO(postBasicDTO)
                .build();
    }

    public void addLike(Like like) {
        likeRepository.save(like);
    }

}
