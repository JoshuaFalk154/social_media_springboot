package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.UserCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserLoginDTO;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

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
    @CsvSource({"username1, email1@gmail.com, password1", "username2, email2@gmail.com, password2"})
    public void UserService_SignUpValidUser_ReturnsCreatedUser(
            String username, String email, String password) {
        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder().username(username).email(email).password(password).build();

        User expectedUser =
                User.builder()
                        .nickname(userCreateDTO.getUsername())
                        .email(userCreateDTO.getEmail())
                        .password("encodedPassword")
                        .build();

        when(passwordEncoder.encode(userCreateDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);

        User createdUser = userService.signup(userCreateDTO);

        Assertions.assertThat(createdUser).isNotNull();
        Assertions.assertThat(createdUser.getNickname()).isEqualTo(expectedUser.getNickname());
        Assertions.assertThat(createdUser.getEmail()).isEqualTo(expectedUser.getEmail());
        Assertions.assertThat(createdUser.getPassword()).isEqualTo(expectedUser.getPassword());
    }

    @Test
    public void UserService_AuthenticateValid_ReturnAuthenticatedUser() {
        UserLoginDTO userLoginDTO = UserFactory.createValidLoginUserDTO();
        User expectedUser =
                User.builder().email(userLoginDTO.getEmail()).password(userLoginDTO.getPassword()).build();

        when(userRepository.findByEmail(userLoginDTO.getEmail())).thenReturn(Optional.of(expectedUser));

        User result = userService.authenticate(userLoginDTO);

        Assertions.assertThat(expectedUser).isEqualTo(result);
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(userLoginDTO.getEmail());
    }

    @Test
    public void UserService_AuthenticateDisabledUser_ThrowBadCredentialsException() {
        UserLoginDTO userLoginDTO = UserFactory.createValidLoginUserDTO();

        doThrow(new DisabledException("User disabled"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        Assertions.assertThatThrownBy(() -> userService.authenticate(userLoginDTO))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("User account is disabled.");
    }

    @Test
    public void UserService_AuthenticateLockedUser_ThrowBadCredentialsException() {
        UserLoginDTO userLoginDTO = UserFactory.createValidLoginUserDTO();

        doThrow(new LockedException("User locked"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        Assertions.assertThatThrownBy(() -> userService.authenticate(userLoginDTO))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("User account is locked.");
    }

    @Test
    public void UserService_AuthenticateInvalidUser_ThrowBadCredentialsException() {
        UserLoginDTO userLoginDTO = UserFactory.createValidLoginUserDTO();

        doThrow(new BadCredentialsException("Authentication failed"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        Assertions.assertThatThrownBy(() -> userService.authenticate(userLoginDTO))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password.");
    }

    @Test
    public void UserService_UnexpectedRuntimeException_ThrowBadCredentialsException() {
        UserLoginDTO userLoginDTO = UserFactory.createValidLoginUserDTO();

        doThrow(new RuntimeException("Something else failed"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        Assertions.assertThatThrownBy(() -> userService.authenticate(userLoginDTO))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Authentication failed. Please try again later.");
    }

    @Test
    public void UserService_GetUserById_ReturnsUser() {
        User excpectedUser = UserFactory.createValidUserWithId(1L);

        when(userRepository.findById(excpectedUser.getId())).thenReturn(Optional.of(excpectedUser));

        User user = userService.getUserById(excpectedUser.getId());

        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getEmail()).isEqualTo(excpectedUser.getEmail());
        Assertions.assertThat(user.getNickname()).isEqualTo(excpectedUser.getNickname());
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
