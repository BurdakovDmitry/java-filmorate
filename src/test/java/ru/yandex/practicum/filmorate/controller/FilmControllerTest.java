package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    public void server() {
        controller = new FilmController();
    }

    @Test
    public void getFilm() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 12))
                .duration(125)
                .build();

        controller.createFilm(film);

        final List<Film> films = controller.findAll().stream().toList();

        assertEquals(1, films.size(), "Пользователи не выводятся");
    }

    @Test
    public void createFilmNameEqualsNull() {
        Film film = Film.builder()
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 12))
                .duration(125)
                .build();

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void createFilmNameBlank() {
        Film film = Film.builder()
                .name("")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 12))
                .duration(125)
                .build();

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void createFilmDescription() {
        Film film = Film.builder()
                .name("Name")
                .description("f".repeat(201))
                .releaseDate(LocalDate.of(1995, 12, 12))
                .duration(125)
                .build();

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void createFilmReleaseDate() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1800, 12, 12))
                .build();

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void createFilmDuration() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 12))
                .duration(-125)
                .build();

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    public void updateFilmIdEqualsNull() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 12))
                .duration(125)
                .build();

        assertThrows(ValidationException.class, () -> controller.updateFilm(film));
    }

    @Test
    public void updateFilmNonFound() {
        Film film = Film.builder()
                .id(5L)
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 12))
                .duration(125)
                .build();

        assertThrows(NotFoundException.class, () -> controller.updateFilm(film));
    }

    @Test
    public void updateFilm() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 12))
                .duration(125)
                .build();

        Film addFilm = controller.createFilm(film);
        addFilm.setName("Film");

        Film updateFilm = controller.updateFilm(addFilm);

        assertEquals("Film", updateFilm.getName(), "Название фильма должно совпадать");
    }
}
