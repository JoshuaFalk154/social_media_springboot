package com.social_media_springboot.social_media_springboot.repositories;

import com.social_media_springboot.social_media_springboot.entities.Post;
import com.social_media_springboot.social_media_springboot.entities.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    private User currentUser;

    @BeforeEach
    public void setup() {
        User user = User.builder()
                .username("falk")
                .email("falk@gmail.com")
                .password("password")
                .build();

        currentUser = userRepository.save(user);
    }

    @Test
    public void UserRepository_Save_ReturnSavedUser() {
//        User user = User.builder()
//                .username("falk")
//                .email("falk@gmail.com")
//                .password("password")
//                .build();

        //User currentUser = userRepository.save(user);

        Post post = Post.builder()
                .title("Test Post")
                .content("This is a test post")
                .owner(currentUser)
                .isPublic(true)
                .build();

        postRepository.save(post);

        User findUser = userRepository.findById(1L).get();




        Assertions.assertThat(currentUser).isNotNull();
        Assertions.assertThat(currentUser.getId()).isEqualTo(1);
        Assertions.assertThat(currentUser.getUsername()).isEqualTo("falk");
        Assertions.assertThat(currentUser.getPosts()).hasSize(1);
        Assertions.assertThat(currentUser.getPosts().get(0).getTitle()).isEqualTo("Test Post");
    }

}
