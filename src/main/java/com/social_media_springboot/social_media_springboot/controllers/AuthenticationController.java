package com.social_media_springboot.social_media_springboot.controllers;

import com.social_media_springboot.social_media_springboot.DTO.LoginResponse;
import com.social_media_springboot.social_media_springboot.DTO.UserCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserLoginDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserResponseDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.mapper.UserMapper;
import com.social_media_springboot.social_media_springboot.services.JwtService;
import com.social_media_springboot.social_media_springboot.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        userService.validateUserNotAlreadyExists(userCreateDTO);

        User registeredUser = userService.signup(userCreateDTO);

        return ResponseEntity.ok(userMapper.userToUserResponseDTO(registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody UserLoginDTO userLoginDto) {
        User authenticatedUser = userService.authenticate(userLoginDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse
                .builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
