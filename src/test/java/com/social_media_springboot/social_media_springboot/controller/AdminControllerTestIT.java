package com.social_media_springboot.social_media_springboot.controller;

import com.social_media_springboot.social_media_springboot.DTO.PostUpdateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserUpdateDTO;
import com.social_media_springboot.social_media_springboot.TestUtil;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.repositories.UserRepository;
import com.social_media_springboot.social_media_springboot.security.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AdminControllerTestIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private User user;
    private String token;


    @Autowired
    public AdminControllerTestIT(MockMvc mockMvc, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setup() throws Exception {
        user = TestUtil.createAndSaveValidUser(TestUtil.getDEFAULT_PASSWORD());
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        token = TestUtil.obtainJwtToken(user, mockMvc);
    }

    @Test
    public void updateUser_ValidAdminAndData_ReturnRightUserResponseDTO() throws Exception {
        User updateUser = TestUtil.createAndSaveValidUser();
        UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                .email("new@mail.com")
                .nickname("newnickname")
                .password("newpassword")
                .build();
        String data = new ObjectMapper().writeValueAsString(userUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/admin/users/" + updateUser.getId(), token, data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updateUser.getId()))
                .andExpect(jsonPath("$.email").value("new@mail.com"))
                .andExpect(jsonPath("$.nickname").value("newnickname"));
    }

    @ParameterizedTest
    @CsvSource({
            "false, false, true",
            "false, true, false",
            "false, true, true",
            "true, false, false",
            "true, false, true",
            "true, true, true"
    })
    @DirtiesContext()
    public void updateUser_ValidAdminValidPartialData_ReturnsRightUserResponseDTO(boolean newEmail, boolean newNickname, boolean newPassword) throws Exception {
        User updateUser = TestUtil.createAndSaveValidUser();
        String email = "new@mail.com";
        String nickname = "newnickname";
        String password = "newPassword";

        UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                .email(newEmail ? email : null)
                .nickname(newNickname ? nickname : null)
                .password(newPassword ? password : null)
                .build();
        String data = new ObjectMapper().writeValueAsString(userUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/admin/users/" + updateUser.getId(), token, data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updateUser.getId()))
                .andExpect(jsonPath("$.email").value(newEmail ? email : updateUser.getEmail()))
                .andExpect(jsonPath("$.nickname").value(newNickname ? nickname : updateUser.getNickname()));

        User updatedUser = userRepository.findById(updateUser.getId()).get();
        Assertions.assertThat(updatedUser.getPassword()).isEqualTo(newPassword ? password : updatedUser.getPassword());
    }

    @Test
    public void updateUser_UserIsNoAdmin_HTTPForbidden() throws Exception {
        User updateUser = TestUtil.createAndSaveValidUser();
        UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                .email("new@mail.com")
                .nickname("newnickname")
                .password("newpassword")
                .build();
        String data = new ObjectMapper().writeValueAsString(userUpdateDTO);

        user.setRole(Role.MANAGER);
        userRepository.save(user);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/admin/users/" + updateUser.getId(), token, data))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUser_ValidAdmin_ReturnSuccess() throws Exception {
        User otherUser = TestUtil.createAndSaveValidUser();

        mockMvc.perform(TestUtil.authorizedRequest("DELETE", "/api/admin/users/" + otherUser.getId(), token))
                .andExpect(status().isOk());
        Optional<User> nonExistendUser = userRepository.findById(otherUser.getId());
        Assertions.assertThat(nonExistendUser).isEmpty();
    }

    @Test
    public void deleteUser_ValidAdminUserNotExist_HTTPNotFound() throws Exception {

        mockMvc.perform(TestUtil.authorizedRequest("DELETE", "/api/admin/users/" + 0, token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUser_NotAdminRights_HTTPForbidden() throws Exception {
        User otherUser = TestUtil.createAndSaveValidUser();
        user.setRole(Role.MANAGER);
        userRepository.save(user);

        mockMvc.perform(TestUtil.authorizedRequest("DELETE", "/api/admin/users/" + otherUser.getId(), token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updatePost_ValidAdminExistingPostValidUpdate_ReturnUpdatedPost() throws Exception {
        User otherUser = TestUtil.createAndSaveValidUser();
        Post post = TestUtil.createAndSaveValidPost(otherUser);
        PostUpdateDTO postUpdateDTO = PostUpdateDTO.builder()
                .title("newtitle")
                .content("newcontent")
                .isPublic(true)
                .build();
        String data = new ObjectMapper().writeValueAsString(postUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/admin/posts/" + post.getId(), token, data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("newtitle"))
                .andExpect(jsonPath("$.content").value("newcontent"))
                .andExpect(jsonPath("$.public").value(true));
    }

    @Test
    public void updatePost_ValidAdminNotExistingPost_HTTPNotFound() throws Exception {
        User otherUser = TestUtil.createAndSaveValidUser();

        PostUpdateDTO postUpdateDTO = PostUpdateDTO.builder()
                .title("newtitle")
                .content("newcontent")
                .isPublic(true)
                .build();
        String data = new ObjectMapper().writeValueAsString(postUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/admin/posts/" + 0, token, data))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePost_NotAdmin_HTTPForbidden() throws Exception {
        User otherUser = TestUtil.createAndSaveValidUser();
        user.setRole(Role.MANAGER);
        userRepository.save(user);

        PostUpdateDTO postUpdateDTO = PostUpdateDTO.builder()
                .title("newtitle")
                .content("newcontent")
                .isPublic(true)
                .build();
        String data = new ObjectMapper().writeValueAsString(postUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/admin/posts/" + 0, token, data))
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    @CsvSource({
            ", ",
            ", content",
            "title, "
    })
    public void updatePost_ValidAdminInvalidData_HTTPBadRequest(String title, String content) throws Exception {
        User otherUser = TestUtil.createAndSaveValidUser();
        Post post = TestUtil.createAndSaveValidPost(otherUser);
        PostUpdateDTO postUpdateDTO = PostUpdateDTO.builder()
                .title(title)
                .content(content)
                .isPublic(true)
                .build();
        String data = new ObjectMapper().writeValueAsString(postUpdateDTO);

        mockMvc.perform(TestUtil.authorizedRequest("PUT", "/api/admin/posts/" + post.getId(), token, data))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserById_ValidAdminAndUserId_ReturnsUser() throws Exception {
        User otherUser = TestUtil.createAndSaveValidUser();

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/admin/users/" + otherUser.getId(), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(otherUser.getId()))
                .andExpect(jsonPath("$.email").value(otherUser.getEmail()))
                .andExpect(jsonPath("$.nickname").value(otherUser.getNickname()));
    }

    @Test
    public void getUserById_ValidManagerAndUserId_ReturnsUser() throws Exception {
        User otherUser = TestUtil.createAndSaveValidUser();
        user.setRole(Role.MANAGER);
        userRepository.save(user);

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/admin/users/" + otherUser.getId(), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(otherUser.getId()))
                .andExpect(jsonPath("$.email").value(otherUser.getEmail()))
                .andExpect(jsonPath("$.nickname").value(otherUser.getNickname()));
    }


    @Test
    public void getUserById_ValidAdminButUserNotExist_HTTPNotFound() throws Exception {
        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/admin/users/" + 0, token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getUserById_UserRole_HTTPForbidden() throws Exception {
        User otherUser = TestUtil.createAndSaveValidUser();
        user.setRole(Role.USER);
        userRepository.save(user);

        mockMvc.perform(TestUtil.authorizedRequest("GET", "/api/admin/users/" + otherUser.getId(), token))
                .andExpect(status().isForbidden());
    }


}
