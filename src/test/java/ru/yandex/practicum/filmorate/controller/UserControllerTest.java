package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTest {
    private UserController controller;

    @BeforeEach
    public void server() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    public void getUser() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        controller.createUser(user);

        final List<User> users = controller.findAll().stream().toList();

        assertEquals(1, users.size(), "Пользователи не выводятся");
    }

    @Test
    public void createUserEmailEqualsNull() {
        User user = new User(null, "Login", "Name",
                LocalDate.of(1995, 12, 12));

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserEmailErrorSyntax() {
        User user = new User("user.email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserEmailDuplicated() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        controller.createUser(user);

        User userDuplicated = new User("user@email.ru", "Login1", "Name1",
                LocalDate.of(1995, 12, 31));

        assertThrows(DuplicatedDataException.class, () -> controller.createUser(userDuplicated));
    }

    @Test
    public void createUserLoginEqualsNull() {
        User user = new User("user@email.ru", null, "Name",
                LocalDate.of(1995, 12, 12));

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserLoginBlank() {
        User user = new User("user@email.ru", "", "Name",
                LocalDate.of(1995, 12, 12));

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserLoginErrorSyntax() {
        User user = new User("user@email.ru", "Login user", "Name",
                LocalDate.of(1995, 12, 12));

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserLoginDuplicated() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        controller.createUser(user);

        User userDuplicated = new User("userDuplicated@email.ru", "Login", "NameDuplicated",
                LocalDate.of(1995, 12, 31));

        assertThrows(DuplicatedDataException.class, () -> controller.createUser(userDuplicated));
    }

    @Test
    public void createUserBirthday() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(2100, 12, 12));

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void updateUserIdEqualsNull() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        assertThrows(ValidationException.class, () -> controller.updateUser(user));
    }

    @Test
    public void updateUserNonFound() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        user.setId(10L);

        assertThrows(NotFoundException.class, () -> controller.updateUser(user));
    }

    @Test
    public void updateUserNameAndLoginEqualsNull() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        controller.createUser(user);

        User newUser = new User("user@email.ru", "", "",
                LocalDate.of(1995, 12, 12));

        newUser.setId(1L);

        assertThrows(ValidationException.class, () -> controller.updateUser(newUser));
    }

    @Test
    public void updateUserEmailDuplicated() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        User user1 = new User("userDuplicated@email.ru", "Login1", "Name1",
                LocalDate.of(1995, 12, 21));

        controller.createUser(user);
        controller.createUser(user1);

        User userDuplicatedEmail = new User("userDuplicated@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        userDuplicatedEmail.setId(1L);

        assertThrows(DuplicatedDataException.class, () -> controller.updateUser(userDuplicatedEmail));
    }

    @Test
    public void updateUserLoginDuplicated() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        User user1 = new User("user1@email.ru", "LoginDuplicated", "Name1",
                LocalDate.of(1995, 12, 21));

        controller.createUser(user);
        controller.createUser(user1);

        User userDuplicatedLogin = new User("user@email.ru", "LoginDuplicated", "Name",
                LocalDate.of(1995, 12, 12));

        userDuplicatedLogin.setId(1L);

        assertThrows(DuplicatedDataException.class, () -> controller.updateUser(userDuplicatedLogin));
    }

    @Test
    public void updateUser() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

        final User newUser = controller.createUser(user);
        newUser.setName("");

        User updateUser = controller.updateUser(newUser);

        assertEquals("Login", updateUser.getName(), "Имя пользователя должно совпадать с логином");
    }
}