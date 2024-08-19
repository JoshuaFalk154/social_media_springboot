package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.LoginUserDTO;
import com.social_media_springboot.social_media_springboot.DTO.RegisterUserDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserBasicDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.exceptions.ResourceNotFoundException;
import com.social_media_springboot.social_media_springboot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;


    public User signup(RegisterUserDTO registerUserDTO) {
        User user = User.builder()
                .username(registerUserDTO.getUsername())
                .email(registerUserDTO.getEmail())
                .password(passwordEncoder.encode(registerUserDTO.getPassword()))
                .build();

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDTO loginUserDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(),
                        loginUserDto.getPassword()
                )
        );

        return userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + "not found"));
    }

    public UserBasicDTO userToUserBasicDTO(User user) {
        return UserBasicDTO.builder()
                .email(user.getEmail())
                .Id(user.getId())
                .build();
    }
}
