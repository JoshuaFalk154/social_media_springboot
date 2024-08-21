package com.social_media_springboot.social_media_springboot.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PostDTO {
    @NotNull
    private String title;
    @NotNull
    private String content;
}