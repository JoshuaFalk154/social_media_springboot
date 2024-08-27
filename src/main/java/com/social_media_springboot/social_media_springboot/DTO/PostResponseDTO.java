package com.social_media_springboot.social_media_springboot.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO extends PostDTO {
    private Long id;
    private boolean isPublic;
    //private UserDTO owner;
    private UserNestedDTO owner;
    private List<LikeResponseDTO> likes;
    private Date createdAt;
    private Date updatedAt;
}
