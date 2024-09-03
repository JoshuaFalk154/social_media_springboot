package com.social_media_springboot.social_media_springboot.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PostCreatedResponseDTO extends PostDTO {
    @NotNull
    private Long id;
    @NotNull
    private Date createdAt;
}