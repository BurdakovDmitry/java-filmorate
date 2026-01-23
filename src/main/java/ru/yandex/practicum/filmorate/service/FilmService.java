package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
	private final FilmStorage filmStorage;
	private final LikeStorage likeStorage;
	private final GenreStorage genreStorage;
	private final DirectorStorage directorStorage;
	private final Validation validation;
	private final FilmMapper filmMapper;

	@Autowired
	public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
					   LikeStorage likeStorage,
					   GenreStorage genreStorage,
					   DirectorStorage directorStorage,
					   Validation validation,
					   FilmMapper filmMapper) {
		this.filmStorage = filmStorage;
		this.likeStorage = likeStorage;
		this.genreStorage = genreStorage;
		this.directorStorage = directorStorage;
		this.validation = validation;
		this.filmMapper = filmMapper;
	}

	public List<FilmDto> findAll() {
		List<Film> films = filmStorage.findAll();

		if (films.isEmpty()) {
			return List.of();
		}

		genreStorage.getGenresForFilms(films);

		log.info("Получен список фильмов: {}", films);
		return films.stream()
			.map(filmMapper::mapToFilmDto)
			.toList();
	}

	public FilmDto getFilmById(Long id) {
		Film film = filmStorage.getFilmById(id)
			.orElseThrow(() -> {
				log.warn("Фильм с id = {} не найден", id);
				return new NotFoundException("Фильм с id = " + id + " не найден");
			});

		film.setGenres(new LinkedHashSet<>(genreStorage.getGenresByFilm(film.getId())));

		log.info("Получен фильм: {}", film);
		return filmMapper.mapToFilmDto(film);
	}

	public FilmDto createFilm(Film film) {
		validation.mpaById(film.getMpa().getId());

		if (film.getGenres() != null && !film.getGenres().isEmpty()) {
			film.getGenres().forEach(genre -> validation.genreById(genre.getId()));
		}

		validation.validateDirectors(film.getDirectors());

		Film newFilm = filmStorage.createFilm(film);
		long filmId = newFilm.getId();

		if (film.getGenres() != null && !film.getGenres().isEmpty()) {
			genreStorage.addGenres(filmId, film.getGenres());
			newFilm.setGenres(new LinkedHashSet<>(film.getGenres()));
		} else {
			newFilm.setGenres(new LinkedHashSet<>());
		}

		if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
			directorStorage.updateFilmDirectors(filmId, film.getDirectors());
			newFilm.setDirectors(new LinkedHashSet<>(film.getDirectors()));
		} else {
			newFilm.setDirectors(new LinkedHashSet<>());
		}

		log.info("Добавлен новый фильм: {}", newFilm);
		return filmMapper.mapToFilmDto(newFilm);
	}

	public FilmDto updateFilm(Film film) {
		validation.filmById(film.getId());
		validation.mpaById(film.getMpa().getId());

		if (film.getGenres() != null && !film.getGenres().isEmpty()) {
			film.getGenres().forEach(genre -> validation.genreById(genre.getId()));
		}

		validation.validateDirectors(film.getDirectors());

		Film updatedFilm = filmStorage.updateFilm(film);
		long filmId = film.getId();

		if (film.getGenres() != null && !film.getGenres().isEmpty()) {
			genreStorage.updateGenres(filmId, film.getGenres());
			updatedFilm.setGenres(new LinkedHashSet<>(film.getGenres()));
		} else {
			updatedFilm.setGenres(new LinkedHashSet<>());
		}

		if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
			directorStorage.updateFilmDirectors(filmId, film.getDirectors());
			updatedFilm.setDirectors(new LinkedHashSet<>(film.getDirectors()));
		} else {
			updatedFilm.setDirectors(new LinkedHashSet<>());
		}

		log.info("Обновлены данные фильма: {}", updatedFilm);
		return filmMapper.mapToFilmDto(updatedFilm);
	}


    public void deleteFilm(Long filmId) {
        validation.filmById(filmId);

        filmStorage.deleteFilm(filmId);
        log.info("Фильм с id = {} успешно удален", filmId);
    }

	public void addLike(Long filmId, Long userId) {
		validation.filmById(filmId);
		validation.userById(userId);

		likeStorage.addLike(filmId, userId);
		log.info("Пользователь c id = {} поставил лайк фильму c id = {}", userId, filmId);
	}

	public void deleteLike(Long filmId, Long userId) {
		validation.filmById(filmId);
		validation.userById(userId);

		likeStorage.deleteLike(filmId, userId);
		log.info("Пользователь c id = {} удалил лайк у фильма c id = {}", userId, filmId);
	}

	public List<FilmDto> getPopularFilms(int count, Integer genreId, Integer year) {
		if (genreId != null) {
			validation.genreById(genreId);
		}

		if (year != null) {
			validation.validateFilmYear(year);
		}

		List<Film> films = filmStorage.getPopularFilms(count, genreId, year);
		genreStorage.getGenresForFilms(films);
		directorStorage.getDirectorsForFilms(films);
		log.info("Получен список из {} самых популярных фильмов по количеству лайков", count);
		if (genreId != null) {
			log.info("Фильтрация по жанру с id = {}", genreId);
		}
		if (year != null) {
			log.info("Фильтрация по году = {}", year);
		}

		return films.stream()
			.map(filmMapper::mapToFilmDto)
			.toList();
	}

	public List<FilmDto> getFilmsByDirector(Long directorId, String sortBy) {
		directorStorage.getDirectorById(directorId)
			.orElseThrow(() -> new NotFoundException("Режиссёр с id = " + directorId + " не найден"));

		List<Film> films;
		if ("year".equals(sortBy)) {
			films = filmStorage.getFilmsByDirectorSortedByYear(directorId);
		} else if ("likes".equals(sortBy)) {
			films = filmStorage.getFilmsByDirectorSortedByLikes(directorId);
		} else {
			films = filmStorage.getFilmsByDirector(directorId);
		}

		genreStorage.getGenresForFilms(films);
		directorStorage.getDirectorsForFilms(films);

		return films.stream()
			.map(filmMapper::mapToFilmDto)
			.collect(Collectors.toList());
	}

	public List<FilmDto> getCommonFilms(Long userId, Long friendId) {

		if (userId.equals(friendId)) {
			log.warn("Запрошены общие фильмы с самим собой для userId = {}", userId);
			throw new DuplicatedDataException("ID пользователя и друга не могут совпадать");
		}

		validation.userById(userId);
		validation.userById(friendId);

		List<Film> films = filmStorage.getCommonFilms(userId, friendId);

		genreStorage.getGenresForFilms(films);

		log.info("Получен список общих фильмов друзей, отсортированных по количеству лайков");

		return films.stream()
			.map(filmMapper::mapToFilmDto)
			.toList();
	}
}
