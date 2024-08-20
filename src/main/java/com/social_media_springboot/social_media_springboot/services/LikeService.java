package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.PostBasicDTO;
import com.social_media_springboot.social_media_springboot.DTO.RequestLikeDTO;
import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.repositories.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final AuthenticationService authenticationService;
    private final LikeRepository likeRepository;



    public RequestLikeDTO likeToRequestLikeDTO(User user , PostBasicDTO postBasicDTO) {
        return RequestLikeDTO.builder()
                .user(authenticationService.userToUserBasicDTO(user))
                .post(postBasicDTO)
                .build();
    }

    public void validateUserNotAlreadyLikedPost(Like like) {
        if (likeRepository.findByUserAndPost(like.getUser(), like.getPost()).isPresent()) {
            throw new IllegalStateException("You already liked post");
        }
    }

    public boolean userLikedPost(Like like) {
        return likeRepository.findByUserAndPost(like.getUser(), like.getPost()).isPresent();
    }

    public void addLike(Like like) {
        likeRepository.save(like);
    }

    public void deleteLike(Like like) {
        likeRepository.delete(like);
    }

    public Optional<Like> findByUserAndPost(User user, Post post) {
        return likeRepository.findByUserAndPost(user, post);
    }

}
