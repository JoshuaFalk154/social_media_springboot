package com.social_media_springboot.social_media_springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media_springboot.social_media_springboot.DTO.PostUpdateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserLoginDTO;
import com.social_media_springboot.social_media_springboot.configs.StaticContextInitializer;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.factory.PostFactory;
import com.social_media_springboot.social_media_springboot.factory.UserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TestUtil {


    public static User createAndSaveValidUser(String password) {
        User user = UserFactory.createValidUser();
        user.setPassword(StaticContextInitializer.getEncoder().encode(password));
        return StaticContextInitializer.getUserRepository().save(user);
    }


    public static String createUserCreateJson(String email, String username, String password) throws Exception {
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .email(email)
                .username(username)
                .password(password)
                .build();
        return new ObjectMapper().writeValueAsString(userCreateDTO);
    }


    public static String createUserLoginJson(String email, String password) throws Exception {
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .email(email)
                .password(password)
                .build();
        return new ObjectMapper().writeValueAsString(userLoginDTO);
    }

    public static Post createAndSaveValidPost(User user) {
        Post post = PostFactory.createValidPost(user, null);
        return StaticContextInitializer.getPostRepository().save(post);
    }

    public static Post createAndSaveValidPostWithTitle(User user, String title) {
        Post post = PostFactory.createValidPost(user, null);
        post.setTitle(title);
        return StaticContextInitializer.getPostRepository().save(post);
    }

    public static PostUpdateDTO createPostUpdateDTO() throws Exception {
        return createPostUpdateDTO("newTitle", "newContent", false);
    }

    public static PostUpdateDTO createPostUpdateDTO(String title, String content, boolean isPublic) throws Exception {
        return PostUpdateDTO.builder()
                .title(title)
                .content(content)
                .isPublic(isPublic)
                .build();
    }


}
