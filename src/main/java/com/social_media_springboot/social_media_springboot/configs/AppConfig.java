package com.social_media_springboot.social_media_springboot.configs;

import com.social_media_springboot.social_media_springboot.DTO.UserCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserLoginDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.security.Role;
import com.social_media_springboot.social_media_springboot.services.JwtService;
import com.social_media_springboot.social_media_springboot.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class AppConfig {
    @Bean
    public CommandLineRunner commandLineRunner(
            UserService userService,
            JwtService jwtService
    ) {
        return args -> {
            // Admin user
            UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                    .email("admin@mail.com")
                    .username("admin")
                    .password("password")
                    .build();
            userService.signupWithRole(userCreateDTO, Role.ADMIN);

            UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                    .email("admin@mail.com")
                    .password("password")
                    .build();

            User authenticatedUser = userService.authenticate(userLoginDTO);

            System.out.println("Admin token: " + jwtService.generateToken(authenticatedUser));

            // Manager user
            UserCreateDTO userCreateDTO2 = UserCreateDTO.builder()
                    .email("manager@mail.com")
                    .username("manager")
                    .password("password")
                    .build();
            userService.signupWithRole(userCreateDTO2, Role.ADMIN);

            UserLoginDTO userLoginDTO2 = UserLoginDTO.builder()
                    .email("manager@mail.com")
                    .password("password")
                    .build();

            User authenticatedUser2 = userService.authenticate(userLoginDTO2);

            System.out.println("Manager token: " + jwtService.generateToken(authenticatedUser2));
        };
    }
}
