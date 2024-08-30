package com.social_media_springboot.social_media_springboot.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserResponseDTO extends UserDTO {
    Long id;
    String email;
    String nickname;
    List<PostNestedDTO> posts;
    List<LikeNestedDTO> likes;
    Date createdAt;
    Date updatedAt;
}
