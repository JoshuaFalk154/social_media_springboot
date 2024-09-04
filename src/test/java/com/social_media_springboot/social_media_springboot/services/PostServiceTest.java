package com.social_media_springboot.social_media_springboot.services;

import com.social_media_springboot.social_media_springboot.DTO.LikeDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostCreateDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostUpdateDTO;
import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.exceptions.ResourceNotFoundException;
import com.social_media_springboot.social_media_springboot.factory.LikeFactory;
import com.social_media_springboot.social_media_springboot.factory.PostFactory;
import com.social_media_springboot.social_media_springboot.factory.UserFactory;
import com.social_media_springboot.social_media_springboot.mapper.PostMapper;
import com.social_media_springboot.social_media_springboot.repositories.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private LikeService likeService;

    @Mock
    private User user;


    @InjectMocks
    private PostService postService;

    @Test
    public void createPost_ValidInput_ReturnsCreatedPost() {
        PostCreateDTO postCreateDTO = PostFactory.createValidCreatePostDTO();
        User user = UserFactory.createValidUser();
        Post expectedPost = Post.builder()
                .title(postCreateDTO.getTitle())
                .content(postCreateDTO.getContent())
                .isPublic(postCreateDTO.isPublic())
                .owner(user)
                .build();
        when(postRepository.save(any(Post.class))).thenReturn(expectedPost);

        Post createdPost = postService.createPost(postCreateDTO, user);

        Assertions.assertThat(createdPost).isNotNull();
        Assertions.assertThat(expectedPost.getTitle()).isEqualTo(createdPost.getTitle());
        Assertions.assertThat(expectedPost.getContent()).isEqualTo(createdPost.getContent());
        Assertions.assertThat(expectedPost.isPublic()).isEqualTo(createdPost.isPublic());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void queryPosts_ExactTitleMatch_ReturnsMatchingPost() {
        Post post1 = Post.builder().id(1L).title("HALLO").content("content1").build();
        Post post2 = Post.builder().id(2L).title("titleabcd").content("content2").build();
        Post post3 = Post.builder().id(3L).title("abdjch").content("content3").build();

        when(user.getPosts()).thenReturn(List.of(post1, post2, post3));
        List<Post> actualList = postService.queryPosts(Optional.empty(), Optional.of("HALLO"), user);

        Assertions.assertThat(actualList).size().isEqualTo(1);
        Assertions.assertThat(actualList.get(0).getId()).isEqualTo(1L);
    }

    @Test
    public void queryPosts_PartialTitleMatch_ReturnsEmptyList() {
        Post post1 = Post.builder().id(1L).title("HALLO").content("content1").build();
        Post post2 = Post.builder().id(2L).title("titleabcd").content("content2").build();
        Post post3 = Post.builder().id(3L).title("abdjch").content("content3").build();

        when(user.getPosts()).thenReturn(List.of(post1, post2, post3));
        List<Post> actualList = postService.queryPosts(Optional.empty(), Optional.of("HAL"), user);

        Assertions.assertThat(actualList).isEmpty();
    }

    @Test
    public void queryPosts_NoTitleProvided_ReturnsAllPosts() {
        Post post1 = Post.builder().id(1L).title("HALLO").content("content1").build();
        Post post2 = Post.builder().id(2L).title("titleabcd").content("content2").build();
        Post post3 = Post.builder().id(3L).title("abdjch").content("content3").build();

        when(user.getPosts()).thenReturn(List.of(post1, post2, post3));
        List<Post> actualList = postService.queryPosts(Optional.empty(), Optional.empty(), user);

        Assertions.assertThat(actualList).size().isEqualTo(3);
        Assertions.assertThat(actualList).containsExactlyInAnyOrder(post1, post2, post3);
    }

    @Test
    public void queryPosts_NoTitleProvidedOneId_ReturnsPostWithId() {
        Post post1 = Post.builder().id(1L).title("HALLO").content("content1").build();
        Post post2 = Post.builder().id(2L).title("titleabcd").content("content2").build();
        Post post3 = Post.builder().id(3L).title("abdjch").content("content3").build();

        when(user.getPosts()).thenReturn(List.of(post1, post2, post3));
        List<Post> actualList = postService.queryPosts(Optional.of(1L), Optional.empty(), user);

        Assertions.assertThat(actualList).size().isEqualTo(1);
        Assertions.assertThat(actualList).containsExactlyInAnyOrder(post1);
    }

    @Test
    public void queryPosts_UserHasNoPosts_ReturnsEmptyList() {
        when(user.getPosts()).thenReturn(new ArrayList<>());
        List<Post> actualList = postService.queryPosts(Optional.empty(), Optional.empty(), user);

        Assertions.assertThat(actualList).size().isEqualTo(0);

    }

    @Test
    public void getPostById_UserOwnsPost_ReturnPost() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post1 = PostFactory.createValidPostWithId(user, null);
        Post post2 = PostFactory.createValidPostWithId(user, null);

        PostService postService1 = Mockito.spy(postService);

        doReturn(post1).when(postService1).validatePostExistenceAndOwnership(any(User.class), any(Long.class));

        Post actualPost = postService1.getPostById(user, post1.getId());

        Assertions.assertThat(actualPost).isEqualTo(post1);
    }


    @Test
    public void isOwner_UserIsOwner_ReturnsTrue() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post = PostFactory.createPost(1L, user, null);

        boolean actualValue = postService.isOwner(user, post);

        Assertions.assertThat(actualValue).isEqualTo(true);
    }

    @Test
    public void isOwner_PostOwnerNull_ReturnFalse() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post = PostFactory.createPost(1L, null, null);
        post.setOwner(null);

        boolean actualValue = postService.isOwner(user, post);

        Assertions.assertThat(actualValue).isEqualTo(false);
    }

    @Test
    public void isOwner_PostNull_ReturnFalse() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post = null;

        boolean actualValue = postService.isOwner(user, post);

        Assertions.assertThat(actualValue).isEqualTo(false);
    }

    @Test
    public void updatePostById_ValidUpdate_ReturnUpdatedPostById() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post = PostFactory.createPost(1L, user, null);
        PostUpdateDTO postUpdateDTO = PostUpdateDTO.builder()
                .title("updated title")
                .content("updated content")
                .isPublic(true).build();

        PostService postService2 = Mockito.spy(postService);


        doReturn(post).when(postService2).validatePostExistenceAndOwnership(user, 1L);
        when(postRepository.save(post)).thenReturn(post);

        Post actualPost = postService2.updatePostById(user, 1L, postUpdateDTO);

        Assertions.assertThat(actualPost.getTitle()).isEqualTo("updated title");
        Assertions.assertThat(actualPost.getContent()).isEqualTo("updated content");
        Assertions.assertThat(actualPost.isPublic()).isEqualTo(true);
    }

    @Test
    public void updatePostById_PostNotFound_updatePostById_PostByIdNotFound_ThrowsResourceNotFoundException() {
        User user = UserFactory.createValidUserWithId(1L);
        PostUpdateDTO postUpdateDTO = PostUpdateDTO.builder().build();

        PostService postService1 = Mockito.spy(postService);

        doThrow(new ResourceNotFoundException("Post not found with id: " + 1L))
                .when(postService1).validatePostExistenceAndOwnership(user, 1L);

        Assertions.assertThatThrownBy(() -> postService1.updatePostById(user, 1L, postUpdateDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updatePostByIdById_UserNotOwner_ThrowsAccessDeniedException() {
        User user = UserFactory.createValidUserWithId(1L);
        Long postId = 1L;
        PostUpdateDTO postUpdateDTO = PostUpdateDTO.builder().build();

        PostService postService1 = Mockito.spy(postService);

        doThrow(new AccessDeniedException("User is not the owner of the post"))
                .when(postService1).validatePostExistenceAndOwnership(user, postId);

        Assertions.assertThatThrownBy(() -> postService1.updatePostById(user, postId, postUpdateDTO))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("User is not the owner of the post");
    }

    @Test
    public void deletePost_DeletePost_PostDeleted() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post1 = PostFactory.createValidPostWithId(user, null);
        Post post2 = PostFactory.createValidPostWithId(user, null);

        List<Post> userPosts = new ArrayList<>(List.of(post1, post2));
        user.setPosts(userPosts);

        PostService postService1 = Mockito.spy(postService);

        doReturn(post1).when(postService1).validatePostExistenceAndOwnership(eq(user), eq(post1.getId()));
        doAnswer(invocation -> {
            Post postToDelete = invocation.getArgument(0);
            user.getPosts().remove(postToDelete);
            return null;
        }).when(postRepository).delete(any(Post.class));


        postService1.deletePost(user, post1.getId());

        Assertions.assertThat(user.getPosts().size()).isEqualTo(1);
        Assertions.assertThat(user.getPosts()).contains(post2);
    }

    @Test
    public void deletePost_PostNotFound_ThrowsResourceNotFoundException() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post1 = PostFactory.createValidPostWithId(user, null);
        Post post2 = PostFactory.createValidPostWithId(user, null);
        List<Post> userPosts = new ArrayList<>(List.of(post1, post2));
        user.setPosts(userPosts);
        PostService postService1 = Mockito.spy(postService);

        doThrow(new ResourceNotFoundException("Post not found with id: 1")).when(postService1).validatePostExistenceAndOwnership(user, post1.getId());

        Assertions.assertThatThrownBy(() -> postService1.deletePost(user, post1.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void deletePost_UserNotOwner_ThrowsAccessDeniedException() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post1 = PostFactory.createValidPostWithId(user, null);
        Post post2 = PostFactory.createValidPostWithId(user, null);
        List<Post> userPosts = new ArrayList<>(List.of(post1, post2));
        user.setPosts(userPosts);
        PostService postService1 = Mockito.spy(postService);

        doThrow(new AccessDeniedException("User is not the owner of the post")).when(postService1).validatePostExistenceAndOwnership(user, post1.getId());

        Assertions.assertThatThrownBy(() -> postService1.deletePost(user, post1.getId()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void toggleLikePost_UserLikesPost_UserAndPostHaveNewLike() {
        User user = UserFactory.createValidUserWithId(1L);
        user.setLikes(new ArrayList<>());
        Post post = PostFactory.createValidPostWithId(user, new ArrayList<>());
        LikeDTO likeDTO = LikeDTO.builder().postId(post.getId()).build();

        PostService postService1 = Mockito.spy(postService);
        doReturn(post).when(postService1).validatePostExistenceAndOwnership(eq(user), eq(post.getId()));
        doReturn(Optional.empty()).when(likeService).findByUserAndPost(user, post);
        doAnswer(invocation -> {
            Like like = invocation.getArgument(0);
            user.addLike(like);
            post.addLike(like);
            return null;
        }).when(likeService).addLike(any(Like.class));

        postService1.toggleLikePost(likeDTO, user);

        Assertions.assertThat(user.getLikes().size()).isEqualTo(1);
        Assertions.assertThat(user.getLikes().get(0)).isEqualTo(post.getLikes().get(0));

    }

    @Test
    public void toggleLikePost_UserUnlikesPost_UserAndPostRemovesLike() {
        User user = UserFactory.createValidUserWithId(1L);
        user.setLikes(new ArrayList<>());
        Post post = PostFactory.createValidPostWithId(user, new ArrayList<>());
        LikeDTO likeDTO = LikeDTO.builder().postId(post.getId()).build();
        Like like = Like.builder().post(post).user(user).build();

        PostService postService1 = Mockito.spy(postService);
        doReturn(post).when(postService1).validatePostExistenceAndOwnership(eq(user), eq(post.getId()));
        doReturn(Optional.of(like)).when(likeService).findByUserAndPost(user, post);
        doAnswer(invocation -> {
            Like like1 = invocation.getArgument(0);
            user.getLikes().remove(like1);
            post.getLikes().remove(like1);
            return null;
        }).when(likeService).deleteLike(any(Like.class));

        postService1.toggleLikePost(likeDTO, user);

        Assertions.assertThat(user.getLikes().size()).isEqualTo(0);
        Assertions.assertThat(post.getLikes().size()).isEqualTo(0);
    }

    @Test
    public void isPostLikedByUser_UserLikedPost_ReturnTrue() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post = PostFactory.createValidPostWithId(user, new ArrayList<>());
        Like like = LikeFactory.createValidLike(user, post);
        user.setLikes(List.of(like));
        user.setPosts(List.of(post));
        post.setLikes(List.of(like));

        PostService postService1 = Mockito.spy(postService);

        doReturn(post).when(postService1).validatePostExistenceAndOwnership(user, post.getId());
        doReturn(Optional.of(like)).when(likeService).findByUserAndPost(eq(user), eq(post));


        boolean actualResult = postService1.isPostLikedByUser(post.getId(), user);

        Assertions.assertThat(actualResult).isEqualTo(true);
    }

    @Test
    public void isPostLikedByUser_UserNotLikedPost_ReturnFalse() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post = PostFactory.createValidPostWithId(user, new ArrayList<>());

        PostService postService1 = Mockito.spy(postService);

        doReturn(post).when(postService1).validatePostExistenceAndOwnership(user, post.getId());
        doReturn(Optional.empty()).when(likeService).findByUserAndPost(eq(user), eq(post));


        boolean actualResult = postService1.isPostLikedByUser(post.getId(), user);

        Assertions.assertThat(actualResult).isEqualTo(false);
    }


    @Test
    public void validatePostExistenceAndOwnership_PostExistAndOwner_ReturnsPost() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post = PostFactory.createValidPostWithId(user, null);

        PostService postService1 = Mockito.spy(postService);

        doReturn(post).when(postService1).getPostById(post.getId());
        doReturn(true).when(postService1).isOwner(user, post);


        Post actualPost = postService1.validatePostExistenceAndOwnership(user, post.getId());

        Assertions.assertThat(actualPost).isEqualTo(post);
    }

    @Test
    public void validatePostExistenceAndOwnership_PostNotExist_ThrowsAccessDeniedException() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post = PostFactory.createValidPostWithId(null, null);

        PostService postService1 = Mockito.spy(postService);

        doReturn(post).when(postService1).getPostById(post.getId());
        doReturn(false).when(postService1).isOwner(user, post);

        Assertions.assertThatThrownBy(() -> postService1.validatePostExistenceAndOwnership(user, post.getId()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void validatePostExistenceAndOwnership_PostNotExist_ThrowsResourceNotFoundException() {
        User user = UserFactory.createValidUserWithId(1L);

        PostService postService1 = Mockito.spy(postService);

        doThrow(new ResourceNotFoundException("Some message")).when(postService1).getPostById(1L);

        Assertions.assertThatThrownBy(() -> postService1.validatePostExistenceAndOwnership(user, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }


}
