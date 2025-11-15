package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    LocalDate presentTime = LocalDate.now();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (user.getEmail() == null) {
            log.warn("Валидация по email не пройдена для {}", user);
            throw new ValidationException("Имейл должен быть указан");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Валидация по email не пройдена для {}", user);
            throw new ValidationException("Некорректный имейл");
        }

        if (!users.isEmpty()) {
            for (User mapUser : users.values()) {
                if (mapUser.getEmail().equals(user.getEmail())) {
                    log.warn("Валидация по email не пройдена для {}", user);
                    throw new DuplicatedDataException("Этот имейл уже используется");
                }
            }
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Валидация по login не пройдена для {}", user);
            throw new ValidationException("Необходимо указать логин без пробелов");
        }

        if (!users.isEmpty()) {
            for (User mapUser : users.values()) {
                if (mapUser.getLogin().equals(user.getLogin())) {
                    log.warn("Валидация по login не пройдена для {}", user);
                    throw new DuplicatedDataException("Этот логин уже используется");
                }
            }
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя = логин {}", user.getLogin());
        }

        if (user.getBirthday() != null) {
            if (user.getBirthday().isAfter(presentTime)) {
                log.warn("Валидация по birthday не пройдена для {}", user);
                throw new ValidationException("Дата рождения не может быть больше " + presentTime);
            }
        }

        user.setId(getNextId());

        log.info("Пользователю {} присвоился id={}.", user.getLogin(), user.getId());

        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            log.warn("Валидация по id не пройдена для {}", user);
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());

            if (user.getEmail() != null && user.getEmail().contains("@")) {
                if (!user.getEmail().equals(oldUser.getEmail())) {
                    //Создаем новый список для проверки уникального логина, исключив переданного пользователя
                    List<User> newUsers = users.values()
                            .stream()
                            .filter(newUser -> newUser.getId() != (long) oldUser.getId())
                            .toList();

                    for (User mapUser : newUsers) {
                        if (mapUser.getEmail().equals(user.getEmail())) {
                            log.warn("Валидация по email не пройдена у {}", user);
                            throw new DuplicatedDataException("Этот имейл уже используется");
                        }
                    }

                    log.info("Был имейл = {}", oldUser.getEmail());
                    oldUser.setEmail(user.getEmail());
                    log.info("Присвоен новый имейл = {}", user.getEmail());
                }
            }

            if (user.getLogin() != null && !user.getLogin().isBlank() && !user.getLogin().contains(" ")) {
                if (!user.getLogin().equals(oldUser.getLogin())) {
                    //Создаем новый список для проверки уникального логина, исключив переданного пользователя
                    List<User> newUsers = users.values()
                            .stream()
                            .filter(newUser -> newUser.getId() != (long) oldUser.getId())
                            .toList();

                    for (User mapUser : newUsers) {
                        if (mapUser.getLogin().equals(user.getLogin())) {
                            log.warn("Валидация по login не пройдена у {}", user);
                            throw new DuplicatedDataException("Этот логин уже используется");
                        }
                    }

                    log.info("Был логин = {}", oldUser.getLogin());
                    oldUser.setLogin(user.getLogin());
                    log.info("Присвоен новый логин = {}", user.getLogin());
                }
            }

            if (user.getName() == null || user.getName().isBlank()) {
                if (user.getLogin() != null && !user.getLogin().isBlank() && !user.getLogin().contains(" ")) {
                    user.setName(user.getLogin());
                    log.info("Так как имя пользователя не указано, то имя = логин {}", user.getLogin());
                } else {
                    log.warn("Валидация по login для name не пройдена у пользователя {}", user);
                    throw new ValidationException("Необходимо указать логин");
                }
            }

            if (user.getName() != null && !user.getName().isBlank()) {
                if (!user.getName().equals(oldUser.getName())) {
                    log.info("Было имя = {}", oldUser.getName());
                    oldUser.setName(user.getName());
                    log.info("Присвоено новое имя = {}", user.getName());
                }
            }

            if (user.getBirthday() != null && user.getBirthday().isBefore(presentTime)) {
                if (!user.getBirthday().equals(oldUser.getBirthday())) {
                    log.info("Старая дата рождения = {}", oldUser.getBirthday());
                    oldUser.setBirthday(user.getBirthday());
                    log.info("Новая дата рождения = {}", user.getBirthday());
                }
            }

            return oldUser;
        }

        log.error("Ошибка поиска пользователя - {}", user);
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
