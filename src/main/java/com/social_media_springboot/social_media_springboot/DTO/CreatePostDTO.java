package com.social_media_springboot.social_media_springboot.DTO;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreatePostDTO extends PostDTO {
    private boolean isPublic;

//    @Builder
//    public CreatePostDTO(String title, String content, boolean isPublic) {
//        super(title, content);
//        this.isPublic = isPublic;
//    }

}
