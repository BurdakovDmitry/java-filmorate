package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({FilmDbStorage.class, FilmRowMapper.class, MpaDbStorage.class, MpaRowMapper.class,
	UserDbStorage.class, UserRowMapper.class, LikeDbStorage.class, GenreDbStorage.class, GenreRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
	private final FilmDbStorage filmStorage;
	private final UserDbStorage userStorage;
	private final LikeDbStorage likeStorage;
	private final GenreDbStorage genreStorage;
	Film film;

	@BeforeEach
	public void createData() {
		film = new Film("Name", "Description",
			LocalDate.of(1995, 12, 12), 12, null);
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
			LocalDate.of(2005, 12, 21), 120, null);

		userStorage.createUser(user);
		filmStorage.createFilm(newFilm);

		likeStorage.addLike(newFilm.getId(), user.getId());

		List<Film> popularFilm = filmStorage.getPopularFilms(5);

		assertThat(popularFilm)
			.hasSize(2)
			.extracting(Film::getName)
			.containsExactly("newName", "Name");
	}

	@Test
	void getPopularFilmsByYear() {
		User user = new User("user@email.ru", "Login", "Name", LocalDate.of(1995, 12, 12));
		User user2 = new User("user2@email.ru", "Login2", "Name2", LocalDate.of(1990, 5, 15));

		Film film2020 = new Film("Film 2020", "Description 2020", LocalDate.of(2020, 1, 1), 100, null);
		Film film2021 = new Film("Film 2021", "Description 2021", LocalDate.of(2021, 1, 1), 120, null);
		Film anotherFilm2020 =
			new Film("Another Film 2020", "Another Description 2020", LocalDate.of(2020, 6, 1), 90, null);

		userStorage.createUser(user);
		userStorage.createUser(user2);

		filmStorage.createFilm(film2020);
		filmStorage.createFilm(film2021);
		filmStorage.createFilm(anotherFilm2020);

		likeStorage.addLike(film2020.getId(), user.getId());
		likeStorage.addLike(film2020.getId(), user2.getId());
		likeStorage.addLike(film2021.getId(), user.getId());
		likeStorage.addLike(anotherFilm2020.getId(), user.getId());

		List<Film> filmsFrom2020 = filmStorage.getPopularFilms(10, null, 2020);

		assertThat(filmsFrom2020)
			.hasSize(2)
			.extracting(Film::getName)
			.containsExactlyInAnyOrder("Film 2020", "Another Film 2020");
	}

	@Test
	void getPopularFilmsByGenreAndYear() {
		User user = new User("user3@email.ru", "Login", "Name", LocalDate.of(1995, 12, 12));
		User user2 = new User("user4@email.ru", "Login2", "Name2", LocalDate.of(1990, 5, 15));

		Genre comedyGenres = new Genre(1, "Комедия");
		Genre dramaGenres = new Genre(2, "Драма");

		Film comedyFilm =
			new Film("Comedy Film", "Comedy Description", LocalDate.of(2020, 1, 1), 100, Set.of(comedyGenres));
		Film dramaFilm =
			new Film("Drama Film", "Drama Description", LocalDate.of(2020, 2, 1), 120, Set.of(dramaGenres));
		Film anotherComedy = new Film("Another Comedy", "Another Comedy Description", LocalDate.of(2020, 3, 1), 90,
			Set.of(comedyGenres));

		userStorage.createUser(user);
		userStorage.createUser(user2);

		Film createdComedy = filmStorage.createFilm(comedyFilm);
		Film createdDrama = filmStorage.createFilm(dramaFilm);
		Film createdAnotherComedy = filmStorage.createFilm(anotherComedy);

		genreStorage.addGenres(createdComedy.getId(), Set.of(comedyGenres));
		genreStorage.addGenres(createdDrama.getId(), Set.of(dramaGenres));
		genreStorage.addGenres(createdAnotherComedy.getId(), Set.of(comedyGenres));

		likeStorage.addLike(createdComedy.getId(), user.getId());
		likeStorage.addLike(createdComedy.getId(), user2.getId());
		likeStorage.addLike(createdDrama.getId(), user.getId());
		likeStorage.addLike(createdAnotherComedy.getId(), user.getId());

		List<Film> comedyFilms = filmStorage.getPopularFilms(10, 1, null);

		assertThat(comedyFilms)
			.hasSize(2)
			.extracting(Film::getName)
			.containsExactlyInAnyOrder("Comedy Film", "Another Comedy");
	}
}