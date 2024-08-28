package com.social_media_springboot.social_media_springboot.mapper;

import com.social_media_springboot.social_media_springboot.DTO.UserNestedDTO;
import com.social_media_springboot.social_media_springboot.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {
    public UserNestedDTO userToNestedUserDTO(User user) {
        return UserNestedDTO.builder()
                .email(user.getEmail())
                .Id(user.getId())
                .build();
    }
}
