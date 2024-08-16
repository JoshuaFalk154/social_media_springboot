package com.social_media_springboot.social_media_springboot.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreatePostResponseDTO extends PostDTO {
    private Long id;
    private Date createdAt;
//
//    @Builder
//    public CreatePostResponseDTO(String title, String content, Long id, Date createdAt) {
//        super(title, content);
//        this.id = id;
//        this.createdAt = createdAt;
//    }


}