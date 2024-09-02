package com.social_media_springboot.social_media_springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media_springboot.social_media_springboot.DTO.LikeDTO;
import com.social_media_springboot.social_media_springboot.TestUtil;
import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class LikeControllerTestIT {

    private final MockMvc mockMvc;
    private User user;
    private String token;


    @Autowired
    public LikeControllerTestIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void setup() throws Exception {
        this.user = TestUtil.createAndSaveValidUser(TestUtil.getDEFAULT_PASSWORD());
        this.token = TestUtil.obtainJwtToken(user, mockMvc);
        TestUtil.setToken(this.token);
    }

    @Test
    public void likePost_RightData_LikesPost() throws Exception {
        Post post = TestUtil.createAndSaveValidPost(user);
        LikeDTO likeDTO = LikeDTO.builder().postId(post.getId()).build();
        String data = new ObjectMapper().writeValueAsString(likeDTO);

        mockMvc.perform(TestUtil.authorizedRequest("POST", "/api/likes", data))
                .andExpect(status().isOk())
                .andExpect(content().string("You liked Post with id " + post.getId()));
    }

    @Test
    public void likePost_RightDataAlreadyLiked_UnlikesPost() throws Exception {
        Post post = TestUtil.createAndSaveValidPost(user);
        Like like = TestUtil.createAndSaveValidLike(user, post);

        LikeDTO likeDTO = LikeDTO.builder().postId(post.getId()).build();
        String data = new ObjectMapper().writeValueAsString(likeDTO);
        mockMvc.perform(TestUtil.authorizedRequest("POST", "/api/likes", data))
                .andExpect(status().isOk())
                .andExpect(content().string("You unliked Post with id " + post.getId()));
    }

    @Test
    public void likePost_UserNotOwner_HTTPForbidden() throws Exception {
        User userOwner = TestUtil.createAndSaveValidUser(TestUtil.getDEFAULT_PASSWORD());
        Post post = TestUtil.createAndSaveValidPost(userOwner);
        LikeDTO likeDTO = LikeDTO.builder().postId(post.getId()).build();
        String data = new ObjectMapper().writeValueAsString(likeDTO);

        mockMvc.perform(TestUtil.authorizedRequest("POST", "/api/likes", data))
                .andExpect(status().isForbidden());
    }

    @Test
    public void likePost_PostNotExist_HTTPNotFound() throws Exception {
        LikeDTO likeDTO = LikeDTO.builder().postId(0L).build();
        String data = new ObjectMapper().writeValueAsString(likeDTO);

        mockMvc.perform(TestUtil.authorizedRequest("POST", "/api/likes", data))
                .andExpect(status().isNotFound());
    }


}
