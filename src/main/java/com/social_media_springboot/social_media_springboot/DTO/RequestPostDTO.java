package com.social_media_springboot.social_media_springboot.DTO;

import com.social_media_springboot.social_media_springboot.entities.Like;
import com.social_media_springboot.social_media_springboot.entities.User;
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
public class RequestPostDTO extends PostDTO{
    private Long id;
    private boolean isPublic;
    private UserDTO owner;
    private List<RequestLikeDTO> likes;
    private Date createdAt;
    private Date updatedAt;
}
