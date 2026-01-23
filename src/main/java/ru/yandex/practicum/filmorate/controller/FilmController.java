package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
	private final FilmService filmService;
	private final Validation validation;

	@GetMapping
	public List<FilmDto> findAll() {
		return filmService.findAll();
	}

	@GetMapping("/{id}")
	public FilmDto getFilmById(@PathVariable Long id) {
		return filmService.getFilmById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FilmDto createFilm(@RequestBody Film film) {
		validation.validationFilm(film);
		return filmService.createFilm(film);
	}

	@PutMapping
	public FilmDto updateFilm(@RequestBody Film film) {
		if (film.getId() == null) {
			log.warn("Валидация по id не пройдена для {}", film);
			throw new ValidationException("Id должен быть указан");
		}

		validation.validationFilm(film);
		return filmService.updateFilm(film);
	}

	@PutMapping("/{id}/like/{userId}")
	public void likeFilm(@PathVariable Long id, @PathVariable Long userId) {
		filmService.addLike(id, userId);
	}

	@DeleteMapping("/{id}/like/{userId}")
	public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
		filmService.deleteLike(id, userId);
	}

	@GetMapping("/popular")
	public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count,
										 @RequestParam(required = false) Integer genreId,
										 @RequestParam(required = false) Integer year) {
		return filmService.getPopularFilms(count, genreId, year);
	}

	@GetMapping("/common")
	public List<FilmDto> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
		return filmService.getCommonFilms(userId, friendId);
	}

	@GetMapping("/director/{directorId}")
	public List<FilmDto> getFilmsByDirector(@PathVariable Long directorId,
											@RequestParam String sortBy) {
		return filmService.getFilmsByDirector(directorId, sortBy);
	}

	@GetMapping("/search")
	public List<FilmDto> searchFilms(@RequestParam String query, @RequestParam String by) {
		return filmService.searchFilms(query, by);
	}
}

