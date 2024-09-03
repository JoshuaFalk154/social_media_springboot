package com.social_media_springboot.social_media_springboot.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    @NotNull
    @NotBlank
    private String token;
    @NotNull
    private long expiresIn;
}
