package com.social_media_springboot.social_media_springboot.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RegisterUserDTO extends UserDTO {
    @NotNull
    private String username;

    @NotNull
    private String password;
}
