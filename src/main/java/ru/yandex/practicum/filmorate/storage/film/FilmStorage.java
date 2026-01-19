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

	List<Film> getFilmsByDirector(Integer directorId);

	List<Film> getFilmsByDirectorSortedByYear(Integer directorId);

	List<Film> getFilmsByDirectorSortedByLikes(Integer directorId);
}
