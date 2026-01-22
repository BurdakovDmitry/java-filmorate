package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Set;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
	private final GenreService genreService;

	@GetMapping()
	public Set<GenreDto> getGenre() {
		return genreService.getGenre();
	}

	@GetMapping("/{id}")
	public GenreDto getGenreById(@PathVariable Integer id) {
		return genreService.getGenreById(id);
	}
}
