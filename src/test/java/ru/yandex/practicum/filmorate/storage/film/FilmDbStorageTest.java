package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({FilmDbStorage.class, FilmRowMapper.class, MpaDbStorage.class, MpaRowMapper.class,
        UserDbStorage.class, UserRowMapper.class, LikeDbStorage.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final LikeDbStorage likeStorage;
    Film film;

    @BeforeEach
    public void createData() {
        film = new Film("Name", "Description",
                LocalDate.of(1995, 12, 12), 125);
        filmStorage.createFilm(film);
    }

    @Test
    void createFilm() {
        assertThat(film)
                .isNotNull()
                .satisfies(filmBase -> assertThat(filmBase.getId()).isPositive());
    }

    @Test
    void findAll() {
        List<Film> films = filmStorage.findAll();

        assertThat(films)
                .isNotEmpty()
                .hasSize(1)
                .extracting(Film::getName)
                .contains("Name");
    }

    @Test
    void getFilmById() {
        Optional<Film> filmOptional = filmStorage.getFilmById(film.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(filmBase -> {
                    assertThat(filmBase.getId()).isEqualTo(film.getId());
                    assertThat(filmBase.getName()).isEqualTo("Name");
                });
    }

    @Test
    public void getFilmUnknownId() {
        Optional<Film> filmOptional = filmStorage.getFilmById(100L);

        assertThat(filmOptional).isEmpty();
    }

    @Test
    void updateFilm() {
        film.setName("newName");
        filmStorage.updateFilm(film);

        Optional<Film> updateFilmOptional = filmStorage.getFilmById(film.getId());

        assertThat(updateFilmOptional)
                .isPresent()
                .hasValueSatisfying(filmBase -> {
                    assertThat(filmBase.getId()).isEqualTo(film.getId());
                    assertThat(filmBase.getName()).isEqualTo("newName");
                });
    }

    @Test
    void getPopularFilms() {
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));
        Film newFilm = new Film("newName", "newDescription",
                LocalDate.of(2005, 12, 21), 120);

        userStorage.createUser(user);
        filmStorage.createFilm(newFilm);

        likeStorage.addLike(newFilm.getId(), user.getId());

        List<Film> popularFilm = filmStorage.getPopularFilms(5);

        assertThat(popularFilm)
                .hasSize(2)
                .extracting(Film::getName)
                .containsExactly("newName", "Name");
    }
}