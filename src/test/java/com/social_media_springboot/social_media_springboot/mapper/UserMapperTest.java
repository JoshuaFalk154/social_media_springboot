package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.UserNestedDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.factory.UserFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @InjectMocks
    UserMapper userMapper;

    @Test
    public void userToNestedUserDTO_User_ReturnsRightObject() {
        User user = UserFactory.createValidUserWithId(1L);
        UserNestedDTO expectedResult = UserNestedDTO.builder()
                .Id(user.getId())
                .email(user.getEmail())
                .build();

        UserNestedDTO actualResult = userMapper.userToNestedUserDTO(user);

        Assertions.assertThat(actualResult).isNotNull();
        Assertions.assertThat(actualResult.getId()).isEqualTo(expectedResult.getId());
        Assertions.assertThat(actualResult.getEmail()).isEqualTo(expectedResult.getEmail());

    }
}
