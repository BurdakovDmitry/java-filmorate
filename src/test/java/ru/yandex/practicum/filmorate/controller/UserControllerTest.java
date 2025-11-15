package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    private UserController controller;

    @BeforeEach
    public void server() {
        controller = new UserController();
    }

    @Test
    public void getUser() {
        User user = User.builder()
                .email("user@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        controller.createUser(user);

        final List<User> users = controller.findAll().stream().toList();

        assertEquals(1, users.size(), "Пользователи не выводятся");
    }

    @Test
    public void createUserEmailEqualsNull() {
        User user = User.builder()
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserEmailErrorSyntax() {
        User user = User.builder()
                .email("user.email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserEmailDuplicated() {
        User user = User.builder()
                .email("user@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        controller.createUser(user);

        User userDuplicated = User.builder()
                .email("user@email.ru")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1995, 12, 31))
                .build();

        assertThrows(DuplicatedDataException.class, () -> controller.createUser(userDuplicated));
    }

    @Test
    public void createUserLoginEqualsNull() {
        User user = User.builder()
                .email("user@email.ru")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserLoginBlank() {
        User user = User.builder()
                .email("user@email.ru")
                .login("")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserLoginErrorSyntax() {
        User user = User.builder()
                .email("user@email.ru")
                .login("Login user")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserLoginDuplicated() {
        User user = User.builder()
                .email("user@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        controller.createUser(user);

        User userDuplicated = User.builder()
                .email("userDuplicated@email.ru")
                .login("Login")
                .name("NameDuplicated")
                .birthday(LocalDate.of(1995, 12, 31))
                .build();

        assertThrows(DuplicatedDataException.class, () -> controller.createUser(userDuplicated));
    }

    @Test
    public void createUserBirthday() {
        User user = User.builder()
                .email("user@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(2100, 12, 12))
                .build();

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void updateUserIdEqualsNull() {
        User user = User.builder()
                .email("user@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        assertThrows(ValidationException.class, () -> controller.updateUser(user));
    }

    @Test
    public void updateUserNonFound() {
        User user = User.builder()
                .id(15L)
                .email("user@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        assertThrows(NotFoundException.class, () -> controller.updateUser(user));
    }

    @Test
    public void updateUserNameAndLoginEqualsNull() {
        User user = User.builder()
                .email("user@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        controller.createUser(user);

        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("")
                .name("")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        assertThrows(ValidationException.class, () -> controller.updateUser(newUser));
    }

    @Test
    public void updateUserEmailDuplicated() {
        User user = User.builder()
                .email("user@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        User user1 = User.builder()
                .email("userDuplicated@email.ru")
                .login("Login1")
                .name("Name1")
                .birthday(LocalDate.of(1995, 12, 13))
                .build();

        controller.createUser(user);
        controller.createUser(user1);

        User userDuplicatedEmail = User.builder()
                .id(1L)
                .email("userDuplicated@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        assertThrows(DuplicatedDataException.class, () -> controller.updateUser(userDuplicatedEmail));
    }

    @Test
    public void updateUserLoginDuplicated() {
        User user = User.builder()
                .email("user@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        User user1 = User.builder()
                .email("user1@email.ru")
                .login("LoginDuplicated")
                .name("Name1")
                .birthday(LocalDate.of(1995, 12, 13))
                .build();

        controller.createUser(user);
        controller.createUser(user1);

        User userDuplicatedLogin = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("LoginDuplicated")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        assertThrows(DuplicatedDataException.class, () -> controller.updateUser(userDuplicatedLogin));
    }

    @Test
    public void updateUser() {
        User user = User.builder()
                .email("user@email.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1995, 12, 12))
                .build();

        final User newUser = controller.createUser(user);
        newUser.setName("");

        User updateUser = controller.updateUser(newUser);

        assertEquals("Login", updateUser.getName(), "Имя пользователя должно совпадать с логином");
    }
}