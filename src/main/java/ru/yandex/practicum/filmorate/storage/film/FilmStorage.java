package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilmById(Long id);

    List<Film> getCommonFilms(Long userId, Long friendId);

    default List<Film> getPopularFilms(int count) {
        return getPopularFilms(count, null, null);
    }

    List<Film> getPopularFilms(int count, Integer genreId, Integer year);
}