package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

@Slf4j
@Component
public class Validation {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private static final int MAX_SIZE_DESCRIPTION = 200;
    private static final LocalDate MINIMUM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public Validation(@Qualifier("userDbStorage") UserStorage userStorage,
                      @Qualifier("filmDbStorage") FilmStorage filmStorage,
                      MpaStorage mpaStorage,
                      GenreStorage genreStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public void userById(Long id) {
        if (userStorage.getUserById(id).isEmpty()) {
            log.warn("Пользователь с id = {} в базе данных не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    public void filmById(Long id) {
        if (filmStorage.getFilmById(id).isEmpty()) {
            log.warn("Фильм с id = {} в базе данных не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    public void mpaById(Integer id) {
        if (mpaStorage.getMpaById(id).isEmpty()) {
            log.warn("Рейтинг MPA с id = {} в базе данных не найден", id);
            throw new NotFoundException("Рейтинг MPA с id = " + id + " не найден");
        }
    }

    public void genreById(Integer id) {
        if (genreStorage.getGenreById(id).isEmpty()) {
            log.warn("Жанр фильма с id = {} в базе данных не найден", id);
            throw new NotFoundException("Жанр фильма с id = " + id + " не найден");
        }
    }

    public void uniqueEmailCreateUser(String email) {
        if (userStorage.getUserByEmail(email).isPresent()) {
            log.warn("Попытка регистрации с уже существующим email: {}", email);
            throw new DuplicatedDataException("Данный имейл уже используется");
        }
    }

    public void uniqueLoginCreateUser(String login) {
        if (userStorage.getUserByLogin(login).isPresent()) {
            log.warn("Попытка регистрации с уже существующим login: {}", login);
            throw new DuplicatedDataException("Данный логин уже используется");
        }
    }

    public void uniqueEmailUpdateUser(String email, Long userId) {
        userStorage.getUserByEmail(email).ifPresent(user -> {
            if (!user.getId().equals(userId)) {
                log.warn("Email {} уже занят другим пользователем", email);
                throw new DuplicatedDataException("Данный имейл уже используется");
            }
        });
    }

    public void uniqueLoginUpdateUser(String login, Long userId) {
        userStorage.getUserByLogin(login).ifPresent(user -> {
            if (!user.getId().equals(userId)) {
                log.warn("Login {} уже занят другим пользователем", login);
                throw new DuplicatedDataException("Данный логин уже используется");
            }
        });
    }

    public void validationUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Валидация по email не пройдена для {}", user);
            throw new ValidationException("Имейл должен быть указан");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Валидация по email не пройдена для {}", user);
            throw new ValidationException("Некорректный имейл");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("У пользователя нет имени, используем логин");
            user.setName(user.getLogin());
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Валидация по login не пройдена для {}", user);
            throw new ValidationException("Необходимо указать логин без пробелов");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация по birthday не пройдена для {}", user);
            throw new ValidationException("Дата рождения не может быть позже " + LocalDate.now());
        }
    }

    public void validationFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация по name не пройдена для {}", film);
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > MAX_SIZE_DESCRIPTION) {
            log.warn("Валидация по description не пройдена для {}", film);
            throw new ValidationException("Было введено " + film.getDescription().length() + " символов. " +
                    "Максимальное количество - 200 символов.");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MINIMUM_RELEASE_DATE)) {
            log.warn("Валидация по releaseDate не пройдена для {}", film);
            throw new ValidationException("Дата не может быть раньше 28.12.1895 года");
        }

        if (film.getDuration() < 0) {
            log.warn("Валидация по duration не пройдена для {}", film);
            throw new ValidationException("Продолжительность фильма не может быть отрицательным");
        }
    }

    public void validateFilmYear(Integer year) {
        if (year < MINIMUM_RELEASE_DATE.getYear()) {
            log.warn("Год {} меньше минимально допустимого {}", year, MINIMUM_RELEASE_DATE.getYear());
            throw new ValidationException("Год выпуска фильма не может быть раньше " + MINIMUM_RELEASE_DATE.getYear());
        }
    }
}
