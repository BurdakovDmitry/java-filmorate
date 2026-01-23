package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
	private final Map<Long, Film> films = new HashMap<>();
	private Long id;

	@Override
	public List<Film> findAll() {
		return films.values().stream().toList();
	}

	@Override
	public Film createFilm(Film film) {
		film.setId(getNextId());

		log.info("Фильму присвоился id={}.", film.getId());

		films.put(film.getId(), film);
		return film;
	}

	@Override
	public Film updateFilm(Film film) {
		if (films.containsKey(film.getId())) {
			Film oldFilm = films.get(film.getId());

			if (!film.getName().equals(oldFilm.getName())) {
				log.info("Было имя = {}", oldFilm.getName());
				oldFilm.setName(film.getName());
				log.info("Присвоено новое имя = {}", film.getName());
			}

			if (!film.getDescription().equals(oldFilm.getDescription())) {
				log.info("Было описание = {}", oldFilm.getDescription());
				oldFilm.setDescription(film.getDescription());
				log.info("Присвоено новое описание = {}", film.getDescription());
			}

			if (film.getDuration() != oldFilm.getDuration()) {
				log.info("Была продолжительность = {}", oldFilm.getDuration());
				oldFilm.setDuration(film.getDuration());
				log.info("Присвоена новая продолжительность = {}", film.getDuration());
			}

			if (!film.getReleaseDate().equals(oldFilm.getReleaseDate())) {
				log.info("Старая дата релиза = {}", oldFilm.getReleaseDate());
				oldFilm.setReleaseDate(film.getReleaseDate());
				log.info("Новая дата релиза = {}", film.getReleaseDate());
			}
			films.put(film.getId(), oldFilm);
			return oldFilm;
		}

		log.error("Ошибка поиска фильма - {}", film);
		throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
	}

	public Optional<Film> getFilmById(Long filmId) {
		if (films.get(filmId) == null) {
			log.error("Ошибка поиска фильма");
			throw new NotFoundException("Фильм с id = " + filmId + " не найден");
		}

		return Optional.ofNullable(films.get(filmId));
	}

	@Override
	public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
		return List.of();
	}

	@Override
	public List<Film> getCommonFilms(Long userId, Long friendId) {
		return List.of();
	}

	@Override
	public List<Film> getFilmsByDirector(Long directorId) {
		return List.of();
	}

	@Override
	public List<Film> getFilmsByDirectorSortedByYear(Long directorId) {
		return List.of();
	}

	@Override
	public List<Film> getFilmsByDirectorSortedByLikes(Long directorId) {
		return List.of();
	}

	@Override
	public List<Film> searchByTitle(String query) {
		return List.of();
	}

	@Override
	public List<Film> searchByDirector(Long directorId) {
		return List.of();
	}

	@Override
	public List<Film> searchByTitleAndDirector(String query, Long directorId) {
		return List.of();
	}

	private long getNextId() {
		if (films.isEmpty()) {
			id = 1L;
			return id;
		} else {
			return ++id;
		}
	}

    @Override
    public void deleteFilm(Long filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Ошибка поиска фильма для удаления - {}", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        Film removedFilm = films.remove(filmId);
        log.info("Фильм удален: {}", removedFilm);
    }
}
