package com.social_media_springboot.social_media_springboot.configs;

import com.social_media_springboot.social_media_springboot.repositories.LikeRepository;
import com.social_media_springboot.social_media_springboot.repositories.PostRepository;
import com.social_media_springboot.social_media_springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StaticContextInitializer {


    private static UserRepository userRepository;
    private static BCryptPasswordEncoder encoder;
    private static PostRepository postRepository;
    private static LikeRepository likeRepository;

    @Autowired
    public StaticContextInitializer(UserRepository userRepository, PostRepository postRepository, BCryptPasswordEncoder encoder, LikeRepository likeRepository) {
        StaticContextInitializer.userRepository = userRepository;
        StaticContextInitializer.encoder = encoder;
        StaticContextInitializer.postRepository = postRepository;
        StaticContextInitializer.likeRepository = likeRepository;
    }

    public static UserRepository getUserRepository() {
        return userRepository;
    }

    public static BCryptPasswordEncoder getEncoder() {
        return encoder;
    }

    public static PostRepository getPostRepository() {
        return postRepository;
    }

    public static LikeRepository getLikeRepository() {
        return likeRepository;
    }
}
