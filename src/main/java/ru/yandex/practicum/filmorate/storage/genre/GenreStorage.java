package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Set<Genre> getGenre();

    Optional<Genre> getGenreById(Integer id);

    void addGenres(Long filmId, Set<Genre> genres);

    void updateGenres(Long filmId, Set<Genre> genres);

    void deleteGenres(Long filmId);

    Set<Genre> getGenresByFilm(Long filmId);

    void getGenresForFilms(List<Film> films);
}
