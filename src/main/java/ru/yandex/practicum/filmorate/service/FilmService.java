package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        log.info("Получен список фильмов: {}", films);
        return films;
    }

    public Film createFilm(Film film) {
        Film newFilm = filmStorage.createFilm(film);
        log.info("Добавлен новый фильм: {}", newFilm);
        return newFilm;
    }

    public Film updateFilm(Film film) {
        Film updateFilm = filmStorage.updateFilm(film);
        log.info("Обновлены данные фильма: {}", updateFilm);
        return updateFilm;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        film.getLikeUsers().add(userId);

        log.info("Пользователь {} поставил лайк фильму \"{}\"", user.getLogin(), film.getName());
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (film.getLikeUsers().contains(userId)) {
            film.getLikeUsers().remove(userId);
            log.info("Пользователь {} удалил лайк у фильма \"{}\"", user.getLogin(), film.getName());
        } else {
            log.info("Ошибка поиска лайка у фильма. Не найден id пользователя");
            throw new NotFoundException(String.format("Пользователь %s не ставил лайк фильму \"%s\"",
                    user.getLogin(), film.getName()));
        }
    }

    public List<Film> getPopularFilms(int count) {
        return findAll().stream()
                .sorted(Comparator.comparing(Film::getNumberLikes).reversed())
                .limit(count)
                .toList();
    }
}
