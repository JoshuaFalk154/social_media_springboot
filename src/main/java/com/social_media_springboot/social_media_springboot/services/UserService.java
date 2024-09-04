package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.UserCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserLoginDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserUpdateDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.exceptions.ResourceNotFoundException;
import com.social_media_springboot.social_media_springboot.repositories.UserRepository;
import com.social_media_springboot.social_media_springboot.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    // TODO added role
    public User signup(UserCreateDTO userCreateDTO) {
        User user = User.builder()
                .nickname(userCreateDTO.getUsername())
                .email(userCreateDTO.getEmail())
                .password(passwordEncoder.encode(userCreateDTO.getPassword()))
                .role(Role.USER)
                .build();


        return userRepository.save(user);
    }

    // TODO not tested
    public User signupWithRole(UserCreateDTO userCreateDTO, Role role) {
        User user = User.builder()
                .nickname(userCreateDTO.getUsername())
                .email(userCreateDTO.getEmail())
                .password(passwordEncoder.encode(userCreateDTO.getPassword()))
                .role(role)
                .build();


        return userRepository.save(user);
    }

    public User authenticate(UserLoginDTO userLoginDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginDto.getEmail(),
                            userLoginDto.getPassword()
                    )
            );
        } catch (DisabledException e) {
            throw new AccessDeniedException("User account is disabled.");
        } catch (LockedException e) {
            throw new AccessDeniedException("User account is locked.");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password.");
        } catch (RuntimeException e) {
            throw new AccessDeniedException("Authentication failed. Please try again later.");
        }

        return userRepository.findByEmail(userLoginDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + "not found"));
    }

    // TODO test
    public void validateUserNotAlreadyExists(UserCreateDTO userCreate) {
        userRepository.findByEmailOrNickname(userCreate.getEmail(), userCreate.getUsername())
                .ifPresent(user -> {
                    throw new IllegalStateException("User already exists");
                });
    }


    /**
     * Updates the user with the given ID using the provided UserUpdateDTO.
     * Assumes the caller has the authority to update the user.
     * Throws ResourceNotFoundException if the user with the given ID does not exist.
     * Only non-null fields in UserUpdateDTO are updated.
     * Returns the updated User.
     */
    public User updateUser(UserUpdateDTO userUpdateDTO, Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " does not exist"));

        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }

        if (userUpdateDTO.getNickname() != null) {
            user.setNickname(userUpdateDTO.getNickname());
        }

        if (userUpdateDTO.getPassword() != null) {
            user.setPassword(userUpdateDTO.getPassword());
        }

        return userRepository.save(user);
    }


    /**
     * if user with id does not exist throw exception
     * otherwise delete user
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " does not exist"));

        userRepository.delete(user);
    }
}
