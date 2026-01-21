package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
	List<Film> findAll();

	Film createFilm(Film film);

	Film updateFilm(Film film);

	Optional<Film> getFilmById(Long id);

	List<Film> getPopularFilms(int count);

	List<Film> getFilmsByDirector(Long directorId);

	List<Film> getFilmsByDirectorSortedByYear(Long directorId);

	List<Film> getFilmsByDirectorSortedByLikes(Long directorId);

	List<Film> searchByTitle(String query);

	List<Film> searchByDirector(Long directorId);

	List<Film> searchByTitleAndDirector(String query, Long directorId);

}
