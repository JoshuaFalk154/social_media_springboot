package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.*;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;


    public CreatePostResponseDTO createPost(CreatePostDTO createPostDTO, User currentUser) {
        Post createPost = Post.builder()
                .title(createPostDTO.getTitle())
                .content(createPostDTO.getContent())
                .isPublic(createPostDTO.isPublic())
                .owner(currentUser)
                .build();


        return postToCreatePostResponseDTO(postRepository.save(createPost));


    }

    public CreatePostResponseDTO postToCreatePostResponseDTO(Post post) {
        return CreatePostResponseDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public RequestPostDTO postToRequestPostDTO(Post post) {
        return RequestPostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .isPublic(post.isPublic())
                .owner(userToUserDTO(post.getOwner()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .build();
    }


    public List<RequestPostDTO> queryPosts(Optional<Long> postId, Optional<String> title, User currentUser) {
        List<Post> posts = currentUser.getPosts();
        return posts.stream()
                .filter(post -> postId.map(id -> post.getId().equals(id)).orElse(true))
                .filter(post -> title.map(post.getTitle()::equals).orElse(true))
                .map(this::postToRequestPostDTO)
                .toList();
    }
}
