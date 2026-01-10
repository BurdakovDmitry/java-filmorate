package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({UserDbStorage.class, UserRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private User user;

    @BeforeEach
    public void createData() {
        user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));
        userStorage.createUser(user);
    }

    @Test
    void createUser() {
        assertThat(user)
                .isNotNull()
                .satisfies(userBase -> assertThat(userBase.getId()).isPositive());
    }

    @Test
    void findAll() {
        List<User> users = userStorage.findAll();

        assertThat(users)
                .isNotEmpty()
                .hasSize(1)
                .extracting(User::getEmail)
                .contains("user@email.ru");
    }

    @Test
    void updateUser() {
        user.setName("newName");
        userStorage.updateUser(user);

        Optional<User> updateUserOptional = userStorage.getUserById(user.getId());

        assertThat(updateUserOptional)
                .isPresent()
                .hasValueSatisfying(userBase -> {
                    assertThat(userBase.getId()).isEqualTo(user.getId());
                    assertThat(userBase.getName()).isEqualTo("newName");
                });
    }

    @Test
    void getUserById() {
        Optional<User> userOptional = userStorage.getUserById(user.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userBase -> {
                    assertThat(userBase.getId()).isEqualTo(user.getId());
                    assertThat(userBase.getName()).isEqualTo("Name");
                });
    }

    @Test
    void getUserByEmail() {
        Optional<User> userOptional = userStorage.getUserByEmail(user.getEmail());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userBase -> {
                    assertThat(userBase.getId()).isEqualTo(user.getId());
                    assertThat(userBase.getName()).isEqualTo("Name");
                });
    }

    @Test
    void getUserByLogin() {
        Optional<User> userOptional = userStorage.getUserByLogin(user.getLogin());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userBase -> {
                    assertThat(userBase.getId()).isEqualTo(user.getId());
                    assertThat(userBase.getName()).isEqualTo("Name");
                });
    }

    @Test
    public void getUserUnknownId() {
        Optional<User> userOptional = userStorage.getUserById(100L);

        assertThat(userOptional).isEmpty();
    }

    @Test
    public void getUserUnknownEmail() {
        Optional<User> userOptional = userStorage.getUserByEmail("email@email");

        assertThat(userOptional).isEmpty();
    }

    @Test
    public void getUserUnknownLogin() {
        Optional<User> userOptional = userStorage.getUserByLogin("Unknown");

        assertThat(userOptional).isEmpty();
    }
}