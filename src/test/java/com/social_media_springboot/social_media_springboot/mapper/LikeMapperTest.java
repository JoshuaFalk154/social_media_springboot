package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.LikeResponseDTO;
import com.social_media_springboot.social_media_springboot.DTO.PostNestedDTO;
import com.social_media_springboot.social_media_springboot.DTO.UserNestedDTO;
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

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
public class LikeMapperTest {

    @Mock
    UserMapper userMapper;

    @Mock
    PostMapper postMapper;

    @InjectMocks
    LikeMapper likeMapper;

    @Test
    public void likeToRequestLikeDTO_Like_ReturnsLikeResponseDTO() {
        User user = UserFactory.createValidUserWithId(1L);
        Post post = PostFactory.createValidPostWithId(user, null);
        Like like = LikeFactory.createValidLike(user, post);
        post.setLikes(List.of(like));

        UserNestedDTO userNestedDTO = UserNestedDTO.builder()
                .Id(user.getId())
                .email(user.getEmail())
                .build();
        PostNestedDTO postNestedDTO = PostNestedDTO.builder()
                .Id(post.getId())
                .title(post.getTitle())
                .content(post.getContent()).build();

        doReturn(userNestedDTO).when(userMapper).userToNestedUserDTO(eq(user));
        doReturn(postNestedDTO).when(postMapper).postToPostNestedDTO(eq(post));


        LikeResponseDTO actualLikeResponseDTO = likeMapper.likeToRequestLikeDTO(like);


        Assertions.assertThat(actualLikeResponseDTO.getUser()).isEqualTo(userNestedDTO);
        Assertions.assertThat(actualLikeResponseDTO.getPost()).isEqualTo(postNestedDTO);

    }
}
