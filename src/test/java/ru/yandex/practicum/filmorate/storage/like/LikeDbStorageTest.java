package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import({LikeDbStorage.class, FilmDbStorage.class, FilmRowMapper.class, UserDbStorage.class, UserRowMapper.class,
        MpaDbStorage.class, MpaRowMapper.class, GenreDbStorage.class, GenreRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LikeDbStorageTest {
	private static final String INSERT_COUNT_QUERY = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
	private final LikeDbStorage likeStorage;
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final JdbcTemplate jdbc;
	private Long filmId;
	private Long userId;

    @BeforeEach
    public void createData() {
        Film film = new Film("Name", "Description",
                LocalDate.of(1995, 12, 12), 125, null);
        User user = new User("user@email.ru", "Login", "Name",
                LocalDate.of(1995, 12, 12));

		filmId = filmStorage.createFilm(film).getId();
		userId = userStorage.createUser(user).getId();
		likeStorage.addLike(filmId, userId);
	}

	@Test
	void addLike() {
		Integer count = jdbc.queryForObject(INSERT_COUNT_QUERY, Integer.class, filmId, userId);

		assertEquals(1, count, "Лайк должен был сохраниться в БД");
	}

	@Test
	void deleteLike() {
		likeStorage.deleteLike(filmId, userId);

		Integer count = jdbc.queryForObject(INSERT_COUNT_QUERY, Integer.class, filmId, userId);

		assertEquals(0, count, "Лайк должен был удалиться из БД");
	}
}