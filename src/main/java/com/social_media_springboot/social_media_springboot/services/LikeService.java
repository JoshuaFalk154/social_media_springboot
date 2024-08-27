package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.mapper.UserMapper;
import com.social_media_springboot.social_media_springboot.repositories.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserService userService;
    private final LikeRepository likeRepository;
    private final UserMapper userMapper;


//    public boolean userLikedPost(Like like) {
//        return likeRepository.findByUserAndPost(like.getUser(), like.getPost()).isPresent();
//    }

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
