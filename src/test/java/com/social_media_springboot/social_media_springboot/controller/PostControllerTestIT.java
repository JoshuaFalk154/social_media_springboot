package com.social_media_springboot.social_media_springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media_springboot.social_media_springboot.DTO.PostCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostUpdateDTO;
import com.social_media_springboot.social_media_springboot.TestUtil;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class PostControllerTestIT {


    private final MockMvc mockMvc;
    private static final String DEFAULT_PASSWORD = "password123";
    private User user;
    private String token;

    @Autowired
    public PostControllerTestIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @BeforeEach
    public void setup() throws Exception {
        user = TestUtil.createAndSaveValidUser(DEFAULT_PASSWORD);
        token = TestUtil.obtainJwtToken(user, mockMvc);
        TestUtil.setToken(token);
    }


    private MockHttpServletRequestBuilder authorizedRequest() {
        return MockMvcRequestBuilders.get("/api/posts")
                .header("Authorization", "Bearer " + token);
    }


    @Test
    public void createPost_CreatePost_ReturnCreatedPost() throws Exception {
        String title = "title";
        String content = "content";
        boolean isPublic = true;
        PostCreateDTO postCreateDTO = PostCreateDTO.builder()
                .isPublic(isPublic)
                .title(title)
                .content(content)
                .build();
        String data = new ObjectMapper().writeValueAsString(postCreateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("POST", "/api/posts", data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.title").value(postCreateDTO.getTitle()))
                .andExpect(jsonPath("$.content").value(postCreateDTO.getContent()));
    }

    @Test
    public void queryPosts_QueryPostsById_ReturnsRightPosts() throws Exception {
        Post post1 = TestUtil.createAndSaveValidPost(user);
        Post post2 = TestUtil.createAndSaveValidPost(user);
        Post post3 = TestUtil.createAndSaveValidPost(user);
        Post post4 = TestUtil.createAndSaveValidPost(user);

        mockMvc.perform(
                        TestUtil.authorizedRequest("GET", "/api/posts", null)
                                .param("post_id", String.valueOf(post2.getId())
                                )
                )
                .andExpect(jsonPath("$[0].id").value(post2.getId()));
    }


    @Test
    public void queryPosts_QueryPostsByTitle_ReturnsRightPosts() throws Exception {
        String title = "XTITLEX";
        Post post1 = TestUtil.createAndSaveValidPostWithTitle(user, title);
        Post post2 = TestUtil.createAndSaveValidPost(user);
        Post post3 = TestUtil.createAndSaveValidPost(user);
        Post post4 = TestUtil.createAndSaveValidPostWithTitle(user, title);

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/posts")
                        .param("title", title)
                )
                .andExpect(jsonPath("$[0].id").value(post1.getId()))
                .andExpect(jsonPath("$[1].id").value(post4.getId()))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void queryPosts_QueryPostByTitleAndId_ReturnsRightPost() throws Exception {
        String title = "XTITLEX";
        Post post1 = TestUtil.createAndSaveValidPostWithTitle(user, title);
        Post post2 = TestUtil.createAndSaveValidPost(user);
        Post post3 = TestUtil.createAndSaveValidPost(user);

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/posts")
                        .param("title", title)
                        .param("post_id", String.valueOf(post1.getId()))
                )
                .andExpect(jsonPath("$[0].id").value(post1.getId()))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void queryPost_QueryWithNoMatchingPost_ReturnsEmpty_1() throws Exception {
        String queryTitle = "test";
        Post post1 = TestUtil.createAndSaveValidPostWithTitle(user, queryTitle + "extra");
        Post post2 = TestUtil.createAndSaveValidPost(user);
        Post post3 = TestUtil.createAndSaveValidPost(user);

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/posts")
                        .param("title", queryTitle)
                        .param("post_id", String.valueOf(post1.getId()))
                )
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void queryPost_QueryWithNoMatchingPost_ReturnsEmpty_2() throws Exception {
        String queryTitle = "test";
        Post post1 = TestUtil.createAndSaveValidPostWithTitle(user, queryTitle);
        Post post2 = TestUtil.createAndSaveValidPost(user);
        Post post3 = TestUtil.createAndSaveValidPost(user);

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/posts")
                        .param("title", queryTitle)
                        .param("post_id", String.valueOf(post1.getId() + 1))
                )
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void queryPost_QueryRightPostButUserNotOwner_ReturnsEmpty() throws Exception {
        User postOwner = TestUtil.createAndSaveValidUser(DEFAULT_PASSWORD);
        String queryTitle = "test";
        Post post1 = TestUtil.createAndSaveValidPostWithTitle(postOwner, queryTitle);

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/posts")
                        .param("title", queryTitle)
                )
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    public void getPostById_IdOfExistingPost_ReturnsPost() throws Exception {
        Post post1 = TestUtil.createAndSaveValidPost(user);
        Post post2 = TestUtil.createAndSaveValidPost(user);

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/posts/" + post1.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post1.getId()))
                .andExpect(jsonPath("$.title").value(post1.getTitle()));
    }

    @Test
    public void getPostById_IdOfNotExistingPost_HTTP404() throws Exception {
        Post post1 = TestUtil.createAndSaveValidPost(user);
        Post post2 = TestUtil.createAndSaveValidPost(user);

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/posts/" + 0)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getPostById_IdOfPostFromOtherUser_HTTP404() throws Exception {
        User postOwner = TestUtil.createAndSaveValidUser(DEFAULT_PASSWORD);
        Post post1 = TestUtil.createAndSaveValidPost(postOwner);
        Post post2 = TestUtil.createAndSaveValidPost(postOwner);

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/posts/" + post1.getId())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void updatePost_RightIdAndObject_ReturnsUpdatedPost() throws Exception {
        Post post1 = TestUtil.createAndSaveValidPost(user);
        PostUpdateDTO postUpdateDTO = TestUtil.createPostUpdateDTO();
        String data = new ObjectMapper().writeValueAsString(postUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/posts/" + post1.getId(), data)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post1.getId()))
                .andExpect(jsonPath("$.title").value(postUpdateDTO.getTitle()))
                .andExpect(jsonPath("$.content").value(postUpdateDTO.getContent()))
                .andExpect(jsonPath("$.public").value(postUpdateDTO.isPublic()));
    }


    @Test
    public void updatePost_PostNotExistRightObject_HTTPNotFound() throws Exception {
        PostUpdateDTO postUpdateDTO = TestUtil.createPostUpdateDTO();
        String data = new ObjectMapper().writeValueAsString(postUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/posts/" + 0, data)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePost_PostExistButWrongObject_HTTPBadRequest() throws Exception {
        Post post1 = TestUtil.createAndSaveValidPost(user);
        PostUpdateDTO postUpdateDTO = TestUtil.createPostUpdateDTO(null, null, true);
        String data = new ObjectMapper().writeValueAsString(postUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/posts/" + post1.getId(), data))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updatePost_PostNotExistButRightObject_HTTPNotFound() throws Exception {
        PostUpdateDTO postUpdateDTO = TestUtil.createPostUpdateDTO();
        String data = new ObjectMapper().writeValueAsString(postUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/posts/" + 0, data))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePost_UserNotOwner_HTTPForbidden() throws Exception {
        User postOwner = TestUtil.createAndSaveValidUser(DEFAULT_PASSWORD);
        Post post = TestUtil.createAndSaveValidPost(postOwner);
        PostUpdateDTO postUpdateDTO = TestUtil.createPostUpdateDTO();
        String data = new ObjectMapper().writeValueAsString(postUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/posts/" + post.getId(), data))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deletePost_RightId_PostDeleted() throws Exception {
        Post post = TestUtil.createAndSaveValidPost(user);

        mockMvc.perform(TestUtil.authorizedRequest("DELETE", "/api/posts/" + post.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void deletePost_UserNotOwnerOfPost_HTTPForbidden() throws Exception {
        User postOwner = TestUtil.createAndSaveValidUser(DEFAULT_PASSWORD);
        Post post = TestUtil.createAndSaveValidPost(postOwner);

        mockMvc.perform(TestUtil.authorizedRequest("DELETE", "/api/posts/" + post.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deletePost_PostNotExist_HTTPNotFound() throws Exception {
        mockMvc.perform(TestUtil.authorizedRequest("DELETE", "/api/posts/" + 0))
                .andExpect(status().isNotFound());
    }
}
