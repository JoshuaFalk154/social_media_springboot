package com.social_media_springboot.social_media_springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.social_media_springboot.social_media_springboot.DTO.PostUpdateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserLoginDTO;
import com.social_media_springboot.social_media_springboot.configs.StaticContextInitializer;
import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.factory.PostFactory;
import com.social_media_springboot.social_media_springboot.factory.UserFactory;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@Service
public class TestUtil {

    @Getter
    private static final String DEFAULT_PASSWORD = "password123";
//    @Setter
//    private static String token;


    public static String obtainJwtToken(User user, String password, MockMvc mockMvc) throws Exception {
        String userLoginJson = TestUtil.createUserLoginJson(user.getEmail(), password);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userLoginJson))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        return JsonPath.read(response, "$.token");
    }

    public static String obtainJwtToken(User user, MockMvc mockMvc) throws Exception {
        return obtainJwtToken(user, DEFAULT_PASSWORD, mockMvc);
    }

    public static MockHttpServletRequestBuilder authorizedRequest(String method, String url, String token, String data) {
        MockHttpServletRequestBuilder requestBuilder = switch (method) {
            case "GET" -> MockMvcRequestBuilders.get(url);
            case "POST" -> MockMvcRequestBuilders.post(url);
            case "PUT" -> MockMvcRequestBuilders.put(url);
            case "DELETE" -> MockMvcRequestBuilders.delete(url);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };

        requestBuilder.header("Authorization", "Bearer " + token);

        if (data != null) {
            requestBuilder.contentType(MediaType.APPLICATION_JSON)
                    .content(data);
        }
        return requestBuilder;
    }

    public static MockHttpServletRequestBuilder authorizedRequest(String method, String url, String token) {
        return authorizedRequest(method, url, token, null);
    }


    public static User createAndSaveValidUser(String password) {
        User user = UserFactory.createValidUser();
        user.setPassword(StaticContextInitializer.getEncoder().encode(password));
        return StaticContextInitializer.getUserRepository().save(user);
    }

    public static User createAndSaveValidUser() {
        User user = UserFactory.createValidUser();
        user.setPassword(StaticContextInitializer.getEncoder().encode(DEFAULT_PASSWORD));
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

    public static Like createAndSaveValidLike(User user, Post post) {
        Like like = Like.builder().
                user(user)
                .post(post)
                .build();
        return StaticContextInitializer.getLikeRepository().save(like);
    }


}
