package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.file.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    public void server() {
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    }

    @Test
    public void getFilm() {
        Film film = new Film("Name", "Description",
                LocalDate.of(1995, 12, 12), 125);

        controller.createFilm(film);

        final List<Film> films = controller.findAll().stream().toList();

        assertEquals(1, films.size(), "Пользователи не выводятся");
    }

    @Test
    public void createFilmNameEqualsNull() {
        Film film = new Film(null, "Description",
                LocalDate.of(1995, 12, 12), 125);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void createFilmNameBlank() {
        Film film = new Film("", "Description",
                LocalDate.of(1995, 12, 12), 125);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void createFilmDescription() {
        Film film = new Film("Name", "f".repeat(201),
                LocalDate.of(1995, 12, 12), 125);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void createFilmReleaseDate() {
        Film film = new Film("Name", "Description",
                LocalDate.of(1800, 12, 12), 120);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void createFilmDuration() {
        Film film = new Film("Name", "Description",
                LocalDate.of(1995, 12, 12), -125);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void updateFilmIdEqualsNull() {
        Film film = new Film("Name", "Description",
                LocalDate.of(1995, 12, 12), 125);

        assertThrows(ValidationException.class, () -> controller.updateFilm(film));
    }

    @Test
    public void updateFilmNonFound() {
        Film film = new Film("Name", "Description",
                LocalDate.of(1995, 12, 12), 125);

        film.setId(5L);

        assertThrows(NotFoundException.class, () -> controller.updateFilm(film));
    }

    @Test
    public void updateFilm() {
        Film film = new Film("Name", "Description",
                LocalDate.of(1995, 12, 12), 125);

        Film addFilm = controller.createFilm(film);
        addFilm.setName("Film");

        Film updateFilm = controller.updateFilm(addFilm);

        assertEquals("Film", updateFilm.getName(), "Название фильма должно совпадать");
    }
}
