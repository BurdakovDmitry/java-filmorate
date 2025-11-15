package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate minimumReleaseDate = LocalDate.of(1895, 12, 28);
    private final int size = 200;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация по name не пройдена для {}", film);
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null) {
            if (film.getDescription().length() > size) {
                log.warn("Валидация по description не пройдена для {}", film);
                throw new ValidationException("Было введено " + film.getDescription().length() + " символов. " +
                        "Максимальное количество - 200 символов.");
            }
        }

        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(minimumReleaseDate)) {
                log.warn("Валидация по releaseDate не пройдена для {}", film);
                throw new ValidationException("Дата не может быть раньше 28.12.1895 года");
            }
        }

        if (film.getDuration() < 0) {
            log.warn("Валидация по duration не пройдена для {}", film);
            throw new ValidationException("Продолжительность фильма не может быть отрицательным");
        }

        film.setId(getNextId());

        log.info("Фильму присвоился id={}.", film.getId());

        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("Валидация по id не пройдена для {}", film);
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());

            if (film.getName() != null && !film.getName().isBlank()) {
                if (!film.getName().equals(oldFilm.getName())) {
                    log.info("Было имя = {}", oldFilm.getName());
                    oldFilm.setName(film.getName());
                    log.info("Присвоено новое имя = {}", film.getName());
                }
            }

            if (film.getDescription() != null) {
                if (film.getDescription().length() <= size) {
                    if (!film.getDescription().equals(oldFilm.getDescription())) {
                        log.info("Было описание = {}", oldFilm.getDescription());
                        oldFilm.setDescription(film.getDescription());
                        log.info("Присвоено новое описание = {}", film.getDescription());
                    }
                }
            }

            if (film.getDuration() >= 0) {
                if (film.getDuration() != oldFilm.getDuration()) {
                    log.info("Была продолжительность = {}", oldFilm.getDuration());
                    oldFilm.setDuration(film.getDuration());
                    log.info("Присвоена новая продолжительность = {}", film.getDuration());
                }
            }

            if (film.getReleaseDate() != null) {
                if (film.getReleaseDate().isAfter(minimumReleaseDate)) {
                    if (!film.getReleaseDate().equals(oldFilm.getReleaseDate())) {
                        log.info("Старая дата релиза = {}", oldFilm.getReleaseDate());
                        oldFilm.setReleaseDate(film.getReleaseDate());
                        log.info("Новая дата релиза = {}", film.getReleaseDate());
                    }
                }
            }

            return oldFilm;
        }

        log.error("Ошибка поиска фильма - {}", film);
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
