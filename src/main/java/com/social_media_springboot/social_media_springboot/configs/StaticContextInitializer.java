package com.social_media_springboot.social_media_springboot.configs;

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

    @Autowired
    public StaticContextInitializer(UserRepository userRepository, PostRepository postRepository, BCryptPasswordEncoder encoder) {
        StaticContextInitializer.userRepository = userRepository;
        StaticContextInitializer.encoder = encoder;
        StaticContextInitializer.postRepository = postRepository;
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
}
