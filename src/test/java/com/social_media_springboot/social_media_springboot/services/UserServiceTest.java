package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.LoginUserDTO;
import com.social_media_springboot.social_media_springboot.DTO.RegisterUserDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.exceptions.ResourceNotFoundException;
import com.social_media_springboot.social_media_springboot.factory.UserFactory;
import com.social_media_springboot.social_media_springboot.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;

import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;


    @ParameterizedTest
    @CsvSource({
            "username1, email1@gmail.com, password1",
            "username2, email2@gmail.com, password2"
    })
    public void UserService_SignUpValidUser_ReturnsCreatedUser(String username, String email, String password) {
        RegisterUserDTO registerUserDTO = RegisterUserDTO.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        User expectedUser = User.builder()
                .username(registerUserDTO.getUsername())
                .email(registerUserDTO.getEmail())
                .password("encodedPassword")
                .build();

        when(passwordEncoder.encode(registerUserDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);

        User createdUser = userService.signup(registerUserDTO);

        Assertions.assertThat(createdUser).isNotNull();
        Assertions.assertThat(createdUser.getUsername()).isEqualTo(expectedUser.getUsername());
        Assertions.assertThat(createdUser.getEmail()).isEqualTo(expectedUser.getEmail());
        Assertions.assertThat(createdUser.getPassword()).isEqualTo(expectedUser.getPassword());
    }

    @ParameterizedTest
    @CsvSource({
            "email1@gmail.com, password1",
            "email2@gmail.com, password2"
    })
    public void UserService_AuthenticateValid_ReturnAuthenticatedUser(String email, String password) {
        LoginUserDTO loginUserDTO = LoginUserDTO.builder()
                .email(email)
                .password(password)
                .build();
        User expectedUser = User.builder()
                .email(loginUserDTO.getEmail())
                .password(loginUserDTO.getPassword())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        User result = userService.authenticate(loginUserDTO);

        Assertions.assertThat(expectedUser).isEqualTo(result);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @ParameterizedTest
    @CsvSource({
            "email1@gmail.com, password1",
            "email2@gmail.com, password2"
    })
    public void UserService_AuthenticateInvalidUser_ThrowBadCredentialsException(String email, String password) {
        LoginUserDTO loginUserDTO = LoginUserDTO.builder()
                .email(email)
                .password(password)
                .build();

        doThrow(new BadCredentialsException("Authentication failed"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        Assertions.assertThatThrownBy(() -> userService.authenticate(loginUserDTO))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password.");
    }

    @Test
    public void UserService_GetUserById_ReturnsUser() {
        User excpectedUser = UserFactory.createValidUserWithId(1L);


        when(userRepository.findById(excpectedUser.getId())).thenReturn(Optional.of(excpectedUser));

        User user = userService.getUserById(excpectedUser.getId());


        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getEmail()).isEqualTo(excpectedUser.getEmail());
        Assertions.assertThat(user.getUsername()).isEqualTo(excpectedUser.getUsername());
    }

    @Test
    public void UserService_GetUserById_ThrowsResourceNotFoundException() {
        Long userId = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id " + userId + "not found");
    }


}
