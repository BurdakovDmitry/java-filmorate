package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Import({GenreDbStorage.class, GenreRowMapper.class, FilmDbStorage.class, FilmRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
	private static final String INSERT_COUNT_QUERY =
		"SELECT COUNT(*) FROM film_genre WHERE film_id = ?";
	private final GenreDbStorage genreStorage;
	private final FilmDbStorage filmStorage;
	private final JdbcTemplate jdbc;
	private Long filmId;
	private Set<Genre> genreSet;

	@BeforeEach
	public void createData() {
		Film film = new Film("Name", "Description",
			LocalDate.of(1995, 12, 12), 125, null);

		filmId = filmStorage.createFilm(film).getId();
		genreSet = new LinkedHashSet<>(List.of(
			new Genre(1, "Комедия"),
			new Genre(2, "Драма")));
		genreStorage.addGenres(filmId, genreSet);
	}

	@Test
	void getGenre() {
		Set<Genre> genres = genreStorage.getGenre();

		assertThat(genres)
			.isNotEmpty()
			.hasSize(6)
			.extracting(Genre::getName)
			.containsExactly("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
	}

	@Test
	void getGenreById() {
		Optional<Genre> genreOptional = genreStorage.getGenreById(1);

		assertThat(genreOptional)
			.isPresent()
			.hasValueSatisfying(genre -> {
				assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
				assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
			});
	}

	@Test
	public void getGenreUnknownId() {
		Optional<Genre> genreOptional = genreStorage.getGenreById(100);

		assertThat(genreOptional).isEmpty();
	}

	@Test
	void addGenres() {
		Integer count = jdbc.queryForObject(INSERT_COUNT_QUERY, Integer.class, filmId);

		assertEquals(2, count, "Запись с жанрами фильма должна быть добавлена в БД");
	}

	@Test
	void getGenresByFilm() {
		Set<Genre> genres = genreStorage.getGenresByFilm(filmId);

		assertThat(genres)
			.isNotEmpty()
			.hasSize(2)
			.extracting(Genre::getName)
			.containsExactly("Комедия", "Драма");
	}

	@Test
	void deleteGenres() {
		genreStorage.deleteGenres(filmId);

		Set<Genre> genres = genreStorage.getGenresByFilm(filmId);

		assertThat(genres).isEmpty();
	}

	@Test
	void updateGenres() {
		genreSet.add(new Genre(3, "Мультфильм"));
		genreStorage.updateGenres(filmId, genreSet);
		Set<Genre> genres = genreStorage.getGenresByFilm(filmId);

		assertThat(genres)
			.isNotEmpty()
			.hasSize(3)
			.extracting(Genre::getName)
			.containsExactly("Комедия", "Драма", "Мультфильм");
	}
}