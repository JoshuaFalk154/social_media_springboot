package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.LikeDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostUpdateDTO;
import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.exceptions.ResourceNotFoundException;
import com.social_media_springboot.social_media_springboot.mapper.PostMapper;
import com.social_media_springboot.social_media_springboot.repositories.PostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Getter
public class PostService {


    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final LikeService likeService;


    public Post createPost(PostCreateDTO postCreateDTO, User currentUser) {
        Post createPost = Post.builder()
                .title(postCreateDTO.getTitle())
                .content(postCreateDTO.getContent())
                .isPublic(postCreateDTO.isPublic())
                .owner(currentUser)
                .build();

        return postRepository.save(createPost);
    }


    public List<Post> queryPosts(Optional<Long> postId, Optional<String> title,
                                 User currentUser) {
        List<Post> posts = currentUser.getPosts();
        return posts.stream()
                .filter(post -> postId.map(id -> post.getId().equals(id)).orElse(true))
                .filter(post -> title.map(post.getTitle()::equals).orElse(true))
                .toList();
    }


    public Post getPostById(User currentUser, Long id) {
        return validatePostExistenceAndOwnership(currentUser, id);
    }

    // TODO test
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " does not exist"));
    }

    public boolean isOwner(User user, Post post) {
        return post != null && user.equals(post.getOwner());
    }

    /**
     * Updates a post while checking if the current user is the owner of the post
     */
    public Post updatePostById(User currentUser, Long id, PostUpdateDTO postUpdateDTO) {
        Post post = validatePostExistenceAndOwnership(currentUser, id);

        post.setTitle(postUpdateDTO.getTitle());
        post.setContent(postUpdateDTO.getContent());
        post.setPublic(postUpdateDTO.isPublic());
        return postRepository.save(post);
    }

    /**
     * Updates a post, assuming the caller has the authority to perform the update
     */
    // TODO test
    public Post updatePostById(Long id, PostUpdateDTO postDTO) {
        Post post = getPostById(id);

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setPublic(postDTO.isPublic());
        return postRepository.save(post);
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
        Post post = getPostById(id);

        if (!isOwner(user, post)) {
            throw new AccessDeniedException("User is not the owner of the post");
        }
        return post;
    }
}
