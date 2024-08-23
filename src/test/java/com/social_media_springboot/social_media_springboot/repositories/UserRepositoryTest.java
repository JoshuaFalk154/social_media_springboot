package com.social_media_springboot.social_media_springboot.repositories;

import com.social_media_springboot.social_media_springboot.entities.User;
import com.social_media_springboot.social_media_springboot.factory.UserFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void save_ValidUser_ReturnsSavedUser() {
        User savedUser = userRepository.save(UserFactory.createValidUser());

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void findAll_ReturnsAllUsers() {
        User user1 = userRepository.save(UserFactory.createValidUser());
        User user2 = userRepository.save(UserFactory.createValidUser());

        List<User> foundUsers = userRepository.findAll();

        Assertions.assertThat(foundUsers).isNotNull();
        Assertions.assertThat(foundUsers.size()).isEqualTo(2);
        Assertions.assertThat(foundUsers).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void findById_ExistingId_ReturnsUserOptional() {
        User savedUser = userRepository.save(UserFactory.createValidUser());

        Optional<User> userOptional = userRepository.findById(savedUser.getId());

        Assertions.assertThat(userOptional).isPresent();
        Assertions.assertThat(userOptional.get()).isEqualTo(savedUser);
    }

    @ParameterizedTest
    @CsvSource({
            "newUsername1",
            "newUsername2"
    })
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void updateUser_ValidUser_ReturnsUpdatedUser(String updatedUsername) {
        User savedUser = userRepository.save(UserFactory.createValidUser());

        savedUser.setUsername(updatedUsername);
        User updatedUser = userRepository.save(savedUser);

        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(updatedUsername);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void deleteById_ExistingId_RemovesUser() {
        User savedUser = userRepository.save(UserFactory.createValidUser());

        userRepository.deleteById(savedUser.getId());
        Optional<User> userOptional = userRepository.findById(savedUser.getId());

        Assertions.assertThat(userOptional).isEmpty();
    }
}