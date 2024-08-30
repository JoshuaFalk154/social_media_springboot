package com.social_media_springboot.social_media_springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media_springboot.social_media_springboot.DTO.UserCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserLoginDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.factory.UserFactory;
import com.social_media_springboot.social_media_springboot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class AuthenticationControllerTestIT {


    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public AuthenticationControllerTestIT(MockMvc mockMvc,
                                          UserRepository userRepository,
                                          BCryptPasswordEncoder encoder
    ) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    private User createAndSaveValidUser(String password) {
        User user = UserFactory.createValidUser();
        user.setPassword(encoder.encode(password));
        return userRepository.save(user);
    }

    private String createUserLoginJson(String email, String password) throws Exception {
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .email(email)
                .password(password)
                .build();
        return new ObjectMapper().writeValueAsString(userLoginDTO);
    }

    private String createUserCreateJson(String email, String username, String password) throws Exception {
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .email(email)
                .username(username)
                .password(password)
                .build();
        return new ObjectMapper().writeValueAsString(userCreateDTO);
    }


    @Test
    @Transactional
    public void register_ValidUser_ReturnsCreatedUser() throws Exception {
        String username = "testuser";
        String email = "testuser@example.com";
        String password = "password123";

        String userCreateDTOJson = createUserCreateJson(email, username, password);

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
        User savedUser = createAndSaveValidUser(originalPassword);
        String email = duplicateEmail ? savedUser.getEmail() : "random@mail.com";
        String username = duplicateUsername ? savedUser.getNickname() : "randomUsername";

        String userCreateDTOJson = createUserCreateJson(email, username, originalPassword);

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
        User savedUser = createAndSaveValidUser(originalPassword);

        String userLoginJson = createUserLoginJson(savedUser.getEmail(), originalPassword);

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
        User savedUser = createAndSaveValidUser(originalPassword);

        String email = rightEmail ? savedUser.getEmail() : "wrong@mail.com";
        String password = rightPassword ? originalPassword : "wrongpassword";

        String userLoginJson = createUserLoginJson(email, password);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userLoginJson)
                )
                .andExpect(status().isBadRequest());
    }
}

