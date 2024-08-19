package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.*;
import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.exceptions.ResourceNotFoundException;
import com.social_media_springboot.social_media_springboot.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class PostService {


    private final PostRepository postRepository;
    private final AuthenticationService authenticationService;


    private final LikeService likeService;


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
                // TODO
                // .likes(post.getLikes().stream().map(likeService::likeToRequestLikeDTO).toList())
                .likes(post.getLikes().stream().map(like -> likeService.likeToRequestLikeDTO(like.getUser(), postToPostBasicDTO(like.getPost()))).toList())
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

    public RequestPostDTO getPostById(User currentUser, Long id) {
        Post post = validatePostExistenceAndOwnership(currentUser, id);
        return postToRequestPostDTO(post);
    }

    public boolean isOwner(User user, Post post) {

        return user.equals(post.getOwner());

    }

    public RequestPostDTO updatePostById(User currentUser, Long id, UpdatePostDTO postDTO) {
        Post post = validatePostExistenceAndOwnership(currentUser, id);

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setPublic(post.isPublic());
        postRepository.save(post);

        return postToRequestPostDTO(post);
    }

    public void deletePost(User currentUser, Long id) {
        Post post = validatePostExistenceAndOwnership(currentUser, id);

        postRepository.delete(post);
    }

    public void likePost(LikeDTO likeDTO, User user) {
        Long postId = likeDTO.getPostId();
        Post post = validatePostExistenceAndOwnership(user, postId);

        Like like = Like.builder()
                        .user(user)
                        .post(post)
                        .build();

        likeService.addLike(like);

        user.addLike(like);
        post.addLike(like);

    }

    public Post validatePostExistenceAndOwnership(User user, Long id) {
//        Post post = postRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        Post post = postRepository.findById(id).get();
        if (!isOwner(user, post)) {
            throw new AccessDeniedException("User is not the owner of the post");
        }
        return post;
    }

    public PostBasicDTO postToPostBasicDTO(Post post) {
        return PostBasicDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .Id(post.getId())
                .build();
    }





}
