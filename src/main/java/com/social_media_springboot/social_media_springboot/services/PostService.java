package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.*;
import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.exceptions.ResourceNotFoundException;
import com.social_media_springboot.social_media_springboot.mapper.PostMapper;
import com.social_media_springboot.social_media_springboot.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;


@Service
@RequiredArgsConstructor
public class PostService {


    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final LikeService likeService;


    public CreatePostResponseDTO createPost(CreatePostDTO createPostDTO, User currentUser) {
        Post createPost = Post.builder()
                .title(createPostDTO.getTitle())
                .content(createPostDTO.getContent())
                .isPublic(createPostDTO.isPublic())
                .owner(currentUser)
                .build();

        Post post = postRepository.save(createPost);


        return postMapper.postToCreatePostResponseDTO(post);
    }


    public List<RequestPostDTO> queryPosts(Optional<Long> postId, Optional<String> title, User currentUser) {
        List<Post> posts = currentUser.getPosts();
        return posts.stream()
                .filter(post -> postId.map(id -> post.getId().equals(id)).orElse(true))
                .filter(post -> title.map(post.getTitle()::equals).orElse(true))
                .map(postMapper::postToRequestPostDTO)
                .toList();
    }

    public RequestPostDTO getPostById(User currentUser, Long id) {
        Post post = validatePostExistenceAndOwnership(currentUser, id);
        return postMapper.postToRequestPostDTO(post);
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

        return postMapper.postToRequestPostDTO(post);
    }

    public void deletePost(User currentUser, Long id) {
        Post post = validatePostExistenceAndOwnership(currentUser, id);

        postRepository.delete(post);
    }

    @Transactional
    public void toggleLikePost(LikeDTO likeDTO, User user) {
        Long postId = likeDTO.getPostId();
        Post post = validatePostExistenceAndOwnership(user, postId);

        Optional<Like> existingLikeOptional = likeService.findByUserAndPost(user, post);

        if (existingLikeOptional.isPresent()) {
            Like existingLike = existingLikeOptional.get();
            likeService.deleteLike(existingLike);
        } else {
            Like newLike = Like.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeService.addLike(newLike);
        }
    }

    public boolean isPostLikedByUser(Long postId, User user) {
        Post post = validatePostExistenceAndOwnership(user, postId);
        return likeService.findByUserAndPost(user, post).isPresent();
    }

    public Post validatePostExistenceAndOwnership(User user, Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        if (!isOwner(user, post)) {
            throw new AccessDeniedException("User is not the owner of the post");
        }
        return post;
    }


}
