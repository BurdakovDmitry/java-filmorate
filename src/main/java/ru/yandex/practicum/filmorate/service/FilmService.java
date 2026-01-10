package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final Validation validation;
    private final FilmMapper filmMapper;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       LikeStorage likeStorage,
                       GenreStorage genreStorage,
                       Validation validation, FilmMapper filmMapper) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
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
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> {
            log.warn("Фильм с id = {} не найден", id);
            return new NotFoundException("Фильм с id = " + id + " не найден");
        });

        film.setGenres(genreStorage.getGenresByFilm(film.getId()));

        log.info("Получен фильм: {}", film);
        return filmMapper.mapToFilmDto(film);
    }

    public FilmDto createFilm(Film film) {
        validation.mpaById(film.getMpa().getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                validation.genreById(genre.getId());
            }
        }

        Film newFilm = filmStorage.createFilm(film);
        long filmId = newFilm.getId();

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.addGenres(filmId, film.getGenres());
            newFilm.setGenres(new LinkedHashSet<>(film.getGenres()));
        } else {
            newFilm.setGenres(new LinkedHashSet<>());
        }

        log.info("Добавлен новый фильм: {}", newFilm);
        return filmMapper.mapToFilmDto(newFilm);
    }

    public FilmDto updateFilm(Film film) {
        validation.filmById(film.getId());
        validation.mpaById(film.getMpa().getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                validation.genreById(genre.getId());
            }
        }

        Film updateFilm = filmStorage.updateFilm(film);
        long filmId = film.getId();

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.updateGenres(filmId, film.getGenres());
            updateFilm.setGenres(new LinkedHashSet<>(film.getGenres()));
        } else {
            updateFilm.setGenres(new LinkedHashSet<>());
        }

        log.info("Обновлены данные фильма: {}", updateFilm);
        return filmMapper.mapToFilmDto(updateFilm);
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

    public List<FilmDto> getPopularFilms(int count) {
        List<Film> films = filmStorage.getPopularFilms(count);

        genreStorage.getGenresForFilms(films);

        log.info("Получен список из {} самых популярных фильмов по количеству лайков", count);
        return films.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }
}
