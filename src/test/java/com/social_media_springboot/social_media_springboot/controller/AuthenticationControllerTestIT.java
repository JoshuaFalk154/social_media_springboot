package com.social_media_springboot.social_media_springboot.controller;

import com.social_media_springboot.social_media_springboot.TestUtil;
import com.social_media_springboot.social_media_springboot.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthenticationControllerTestIT {


    private final MockMvc mockMvc;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    @Autowired
    public AuthenticationControllerTestIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @Transactional
    public void register_ValidUser_ReturnsCreatedUser() throws Exception {
        String username = "testuser";
        String email = "testuser@example.com";
        String password = "password123";

        String userCreateDTOJson = TestUtil.createUserCreateJson(email, username, password);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userCreateDTOJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.nickname").value(username));
    }

    @ParameterizedTest
    @CsvSource({
            "true, true",
            "false, true",
            "true, false"
    })
    @Transactional
    public void register_UserAlreadyExists_HTTPConflict(boolean duplicateEmail, boolean duplicateUsername) throws Exception {
        String originalPassword = "password123";
        User savedUser = TestUtil.createAndSaveValidUser(originalPassword);
        String email = duplicateEmail ? savedUser.getEmail() : "random@mail.com";
        String username = duplicateUsername ? savedUser.getNickname() : "randomUsername";

        String userCreateDTOJson = TestUtil.createUserCreateJson(email, username, originalPassword);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userCreateDTOJson)
                )
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void authenticate_ValidUser_ReturnsJWTToken() throws Exception {
        String originalPassword = "password123";
        User savedUser = TestUtil.createAndSaveValidUser(originalPassword);

        String userLoginJson = TestUtil.createUserLoginJson(savedUser.getEmail(), originalPassword);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userLoginJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expiresIn").exists());
    }

    @ParameterizedTest
    @CsvSource({
            "false, false",
            "false, true",
            "true, false"
    })
    @Transactional
    public void authenticate_WrongCredentials_HTTPBadRequest(boolean rightEmail, boolean rightPassword) throws Exception {
        String originalPassword = "password123";
        User savedUser = TestUtil.createAndSaveValidUser(originalPassword);

        String email = rightEmail ? savedUser.getEmail() : "wrong@mail.com";
        String password = rightPassword ? originalPassword : "wrongpassword";

        String userLoginJson = TestUtil.createUserLoginJson(email, password);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userLoginJson)
                )
                .andExpect(status().isBadRequest());
    }
}

