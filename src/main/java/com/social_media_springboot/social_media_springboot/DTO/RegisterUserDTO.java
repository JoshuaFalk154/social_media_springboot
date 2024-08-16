package com.social_media_springboot.social_media_springboot.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDTO extends UserDTO {
    private String username;
    private String password;
}
