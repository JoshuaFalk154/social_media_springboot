package com.social_media_springboot.social_media_springboot.factory;

import com.social_media_springboot.social_media_springboot.DTO.LoginUserDTO;
import com.social_media_springboot.social_media_springboot.entities.User;

public class UserFactory {

  private static int userCounter = 1;

  public static User createValidUser() {
    return createValidUser(
        "validUser" + userCounter++,
        "valid" + userCounter + "@gmail.com",
        "password" + userCounter);
  }

  public static User createValidUser(String username, String email, String password) {
    return createUser(username, email, password);
  }

  public static User createUser(String username, String email, String password) {
    return User.builder().username(username).email(email).password(password).build();
  }

  public static User createValidUserWithId(Long id) {
    return createValidUserWithId(
        id, "validUser" + id, "valid" + id + "@gmail.com", "password" + id);
  }

  public static User createValidUserWithId(
      Long id, String username, String email, String password) {
    User user = createUser(username, email, password);
    user.setId(id);
    return user;
  }

  public static LoginUserDTO createValidLoginUserDTO() {
    return createValidLoginUserDTO("valid" + userCounter + "@gmail.com", "password" + userCounter);
  }

  public static LoginUserDTO createValidLoginUserDTO(String email, String password) {
    return LoginUserDTO.builder().email(email).password(password).build();
  }
}
