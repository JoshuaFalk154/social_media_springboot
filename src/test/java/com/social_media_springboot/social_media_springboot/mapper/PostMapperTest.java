package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.*;
import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.factory.LikeFactory;
import com.social_media_springboot.social_media_springboot.factory.PostFactory;
import com.social_media_springboot.social_media_springboot.factory.UserFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;


@ExtendWith(MockitoExtension.class)
public class PostMapperTest {

    @Mock
    LikeMapper likeMapper;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    PostMapper postMapper;

    @Test
    public void postToCreatePostResponseDTO_Post_ReturnsRightObject() {
        Post post = PostFactory.createValidPostWithId(null, null);
        post.setCreatedAt(Date.from(Instant.now()));

        PostCreatedResponseDTO expectedResult = PostCreatedResponseDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .build();

        PostCreatedResponseDTO actualResult = postMapper.postToCreatePostResponseDTO(post);

        Assertions.assertThat(actualResult.getId()).isEqualTo(expectedResult.getId());
        Assertions.assertThat(actualResult.getTitle()).isEqualTo(expectedResult.getTitle());
        Assertions.assertThat(actualResult.getContent()).isEqualTo(expectedResult.getContent());

    }

    @Test
    public void postToRequestPostDTO_Post_ReturnsRightObject() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post = PostFactory.createValidPostWithId(user, null);
        post.setCreatedAt(Date.from(Instant.from(Instant.now())));
        post.setCreatedAt(null);
        Like like1 = LikeFactory.createValidLike(user, post);
        Like like2 = LikeFactory.createValidLike(user, post);
        post.setLikes(List.of(like1, like2));

        UserNestedDTO userNestedDTO = UserNestedDTO.builder()
                .Id(user.getId())
                .email(user.getEmail())
                .build();
        LikeResponseDTO likeResponseDTO = LikeResponseDTO.builder()
                .user(UserNestedDTO.builder()
                        .Id(user.getId())
                        .email(user.getEmail())
                        .build()
                ).post(
                        PostNestedDTO.builder()
                                .Id(post.getId())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .build()
                ).build();

        PostResponseDTO expectedResult = PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .isPublic(post.isPublic())
                .owner(userNestedDTO)
                .likes(List.of(likeResponseDTO, likeResponseDTO))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();

        doReturn(userNestedDTO).when(userMapper).userToNestedUserDTO(eq(user));
        doReturn(likeResponseDTO).when(likeMapper).likeToRequestLikeDTO(any(Like.class));

        PostResponseDTO actualResult = postMapper.postToRequestPostDTO(post);

        Assertions.assertThat(actualResult.getId()).isEqualTo(expectedResult.getId());
        Assertions.assertThat(actualResult.isPublic()).isEqualTo(expectedResult.isPublic());
        Assertions.assertThat(actualResult.getOwner()).isEqualTo(expectedResult.getOwner());
        Assertions.assertThat(actualResult.getLikes()).isEqualTo(expectedResult.getLikes());
        Assertions.assertThat(actualResult.getCreatedAt()).isEqualTo(expectedResult.getCreatedAt());


    }
}
