package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
	private final GenreStorage genreStorage;
	private final GenreMapper genreMapper;

	public Set<GenreDto> getGenre() {
		Set<GenreDto> genre = genreStorage.getGenre()
			.stream()
			.map(genreMapper::mapToGenreDto)
			.collect(Collectors.toCollection(LinkedHashSet::new));

		log.debug("Получен список жанров: {}", genre);
		return genre;
	}

	public GenreDto getGenreById(Integer id) {
		return genreStorage.getGenreById(id)
			.map(genreMapper::mapToGenreDto)
			.orElseThrow(() -> {
				log.warn("Жанр с id = {} не найден", id);
				return new NotFoundException("Жанр фильма с id = " + id + " не найден");
			});
	}
}
