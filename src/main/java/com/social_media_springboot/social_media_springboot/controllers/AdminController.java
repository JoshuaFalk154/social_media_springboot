package com.social_media_springboot.social_media_springboot.controllers;

import com.social_media_springboot.social_media_springboot.DTO.PostResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostUpdateDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserUpdateDTO;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.mapper.PostMapper;
import com.social_media_springboot.social_media_springboot.mapper.UserMapper;
import com.social_media_springboot.social_media_springboot.services.PostService;
import com.social_media_springboot.social_media_springboot.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PostService postService;
    private final PostMapper postMapper;


    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update User")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        User updatedUseruser = userService.updateUser(userUpdateDTO, id);

        return ResponseEntity.ok(userMapper.userToUserResponseDTO(updatedUseruser));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete User")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.ok("User with id " + id + " successfully deleted.");
    }

    @PutMapping("/posts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Post")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @Valid @RequestBody PostUpdateDTO postUpdateDTO) {
        Post post = postService.updatePostById(id, postUpdateDTO);

        return ResponseEntity.ok(postMapper.postToPostResponseDTO(post));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('admin:read')")
    @Operation(summary = "Get User by id")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(userMapper.userToUserResponseDTO(user));
    }


}
