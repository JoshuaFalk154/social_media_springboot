package com.social_media_springboot.social_media_springboot.configs;

import com.social_media_springboot.social_media_springboot.DTO.UserCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserLoginDTO;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.repositories.PostRepository;
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
            JwtService jwtService,
            PostRepository postRepository
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
            userService.signupWithRole(userCreateDTO2, Role.MANAGER);

            UserLoginDTO userLoginDTO2 = UserLoginDTO.builder()
                    .email("manager@mail.com")
                    .password("password")
                    .build();

            User authenticatedUser2 = userService.authenticate(userLoginDTO2);

            System.out.println("Manager token: " + jwtService.generateToken(authenticatedUser2));

            // User user1
            UserCreateDTO userCreateDTO3 = UserCreateDTO.builder()
                    .email("user1@mail.com")
                    .username("user1")
                    .password("password")
                    .build();
            userService.signupWithRole(userCreateDTO3, Role.USER);

            UserLoginDTO userLoginDTO3 = UserLoginDTO.builder()
                    .email("user1@mail.com")
                    .password("password")
                    .build();

            User authenticatedUser3 = userService.authenticate(userLoginDTO3);

            System.out.println("User1 token: " + jwtService.generateToken(authenticatedUser3));

            // User user2
            UserCreateDTO userCreateDTO4 = UserCreateDTO.builder()
                    .email("user2@mail.com")
                    .username("user2")
                    .password("password")
                    .build();
            userService.signupWithRole(userCreateDTO4, Role.USER);

            UserLoginDTO userLoginDTO4 = UserLoginDTO.builder()
                    .email("user2@mail.com")
                    .password("password")
                    .build();

            User authenticatedUser4 = userService.authenticate(userLoginDTO4);
            Post post1 = Post.builder()
                    .title("sometitle")
                    .content("somecontent")
                    .owner(authenticatedUser3)
                    .isPublic(true).build();
            postRepository.save(post1);


            System.out.println("User2 token: " + jwtService.generateToken(authenticatedUser4));
        };
    }
}
