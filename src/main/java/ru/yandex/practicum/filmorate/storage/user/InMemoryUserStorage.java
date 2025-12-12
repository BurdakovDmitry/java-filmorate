package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static final LocalDate PRESENT_TIME = LocalDate.now();
    private Long id;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
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

        if (user.getBirthday() != null && user.getBirthday().isAfter(PRESENT_TIME)) {
            log.warn("Валидация по birthday не пройдена для {}", user);
            throw new ValidationException("Дата рождения не может быть больше " + PRESENT_TIME);
        }

        user.setId(getNextId());

        log.info("Пользователю {} присвоился id={}.", user.getLogin(), user.getId());

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            log.warn("Валидация по id не пройдена для {}", user);
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());

            if (user.getEmail() != null && user.getEmail().contains("@") &&
                    !user.getEmail().equals(oldUser.getEmail())) {
                //Создаем новый список для проверки уникального имейла, исключив переданного пользователя
                List<User> newUsers = listWithoutOriginalUser(users, oldUser);

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

            if (user.getLogin() != null && !user.getLogin().isBlank() && !user.getLogin().contains(" ") &&
                    !user.getLogin().equals(oldUser.getLogin())) {
                //Создаем новый список для проверки уникального логина, исключив переданного пользователя
                List<User> newUsers = listWithoutOriginalUser(users, oldUser);

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

            if (user.getName() == null || user.getName().isBlank()) {
                if (user.getLogin() != null && !user.getLogin().isBlank() && !user.getLogin().contains(" ")) {
                    user.setName(user.getLogin());
                    log.info("Так как имя пользователя не указано, то имя = логин {}", user.getLogin());
                } else {
                    log.warn("Валидация по login для name не пройдена у пользователя {}", user);
                    throw new ValidationException("Необходимо указать логин");
                }
            }

            if (user.getName() != null && !user.getName().isBlank() && !user.getName().equals(oldUser.getName())) {
                log.info("Было имя = {}", oldUser.getName());
                oldUser.setName(user.getName());
                log.info("Присвоено новое имя = {}", user.getName());
            }

            if (user.getBirthday() != null && user.getBirthday().isBefore(PRESENT_TIME) &&
                    !user.getBirthday().equals(oldUser.getBirthday())) {
                log.info("Старая дата рождения = {}", oldUser.getBirthday());
                oldUser.setBirthday(user.getBirthday());
                log.info("Новая дата рождения = {}", user.getBirthday());
            }

            return oldUser;
        }

        log.error("Ошибка поиска пользователя - {}", user);
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    @Override
    public User getUserById(Long userId) {
        if (users.get(userId) == null) {
            log.error("Ошибка поиска фильма");
            throw new NotFoundException("Фильм с id = " + userId + " не найден");
        }

        return users.get(userId);
    }

    private long getNextId() {
        if (users.isEmpty()) {
            id = 1L;
            return id;
        } else {
            return ++id;
        }
    }

    private List<User> listWithoutOriginalUser(Map<Long, User> users, User oldUser) {
        return users.values()
                .stream()
                .filter(newUser -> newUser.getId() != (long) oldUser.getId())
                .toList();
    }
}
