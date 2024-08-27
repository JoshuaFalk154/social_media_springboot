package com.social_media_springboot.social_media_springboot.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class LikeResponseDTO {
    private UserNestedDTO user;
    private PostNestedDTO post;
}
