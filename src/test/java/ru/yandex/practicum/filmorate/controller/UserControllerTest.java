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
    private User user;
    private User newUser;
    private User duplicatedUser;
    private UserController controller;

    @BeforeEach
    public void server() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
        user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));
        newUser = new User("newUser@email.ru", "NewLogin", "Name",
                LocalDate.of(1995, 12, 12));
        duplicatedUser = new User("duplicatedUser@email.ru", "DuplicatedLogin", "Name",
                LocalDate.of(1995, 12, 12));
    }

    @Test
    public void getUser() {
        controller.createUser(user);

        final List<User> users = controller.findAll().stream().toList();

        assertEquals(1, users.size(), "Пользователи не выводятся");
    }

    @Test
    public void createUserEmailEqualsNull() {
        user.setEmail(null);

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserEmailErrorSyntax() {
        user.setEmail("user.email.ru");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserEmailDuplicated() {
        controller.createUser(user);

        User userDuplicated = new User("user@email.ru", "Login1", "Name1",
                LocalDate.of(1995, 12, 31));

        assertThrows(DuplicatedDataException.class, () -> controller.createUser(userDuplicated));
    }

    @Test
    public void createUserLoginEqualsNull() {
        user.setLogin(null);

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserLoginBlank() {
        user.setLogin("");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserLoginErrorSyntax() {
        user.setLogin("Login user");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void createUserLoginDuplicated() {
        controller.createUser(user);

        User userDuplicated = new User("userDuplicated@email.ru", "Login", "NameDuplicated",
                LocalDate.of(1995, 12, 31));

        assertThrows(DuplicatedDataException.class, () -> controller.createUser(userDuplicated));
    }

    @Test
    public void createUserBirthday() {
        user.setBirthday(LocalDate.of(2100, 12, 12));

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    public void updateUserIdEqualsNull() {
        assertThrows(ValidationException.class, () -> controller.updateUser(user));
    }

    @Test
    public void updateUserNonFound() {
        user.setId(10L);

        assertThrows(NotFoundException.class, () -> controller.updateUser(user));
    }

    @Test
    public void updateUserNameAndLoginEqualsNull() {
        controller.createUser(user);

        newUser.setId(1L);
        newUser.setLogin("");
        newUser.setName("");

        assertThrows(ValidationException.class, () -> controller.updateUser(newUser));
    }

    @Test
    public void updateUserEmailDuplicated() {
        controller.createUser(user);
        controller.createUser(newUser);

        duplicatedUser.setId(1L);
        duplicatedUser.setEmail("newUser@email.ru");

        assertThrows(DuplicatedDataException.class, () -> controller.updateUser(duplicatedUser));
    }

    @Test
    public void updateUserLoginDuplicated() {
        controller.createUser(user);
        controller.createUser(newUser);

        duplicatedUser.setId(1L);
        duplicatedUser.setLogin("NewLogin");

        assertThrows(DuplicatedDataException.class, () -> controller.updateUser(duplicatedUser));
    }

    @Test
    public void updateUser() {
        final User newUser = controller.createUser(user);
        newUser.setName("");

        User updateUser = controller.updateUser(newUser);

        assertEquals("Login", updateUser.getName(), "Имя пользователя должно совпадать с логином");
    }
}