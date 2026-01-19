package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
	private final DirectorStorage directorStorage;
	private final DirectorMapper directorMapper;

	public Set<DirectorDto> getDirectors() {
		Set<DirectorDto> directors = directorStorage.getDirectors().stream()
			.map(directorMapper::mapToDirectorDto)
			.collect(Collectors.toCollection(LinkedHashSet::new));

		log.debug("Получено {} режиссёров", directors.size());
		return directors;
	}

	public DirectorDto getDirectorById(Integer id) {
		return directorStorage.getDirectorById(id)
			.map(directorMapper::mapToDirectorDto)
			.orElseThrow(() -> {
				log.warn("Режиссёр с id = {} не найден", id);
				return new NotFoundException("Режиссёр с id = " + id + " не найден");
			});
	}

	public DirectorDto createDirector(DirectorDto directorDto) {
		var director = directorMapper.mapToDirector(directorDto);
		var createdDirector = directorStorage.createDirector(director);
		return directorMapper.mapToDirectorDto(createdDirector);
	}

	public DirectorDto updateDirector(DirectorDto directorDto) {
		log.info("Обновление режиссёра: {}", directorDto);
		var director = directorMapper.mapToDirector(directorDto);

		var updatedDirector = directorStorage.updateDirector(director)
			.orElseThrow(() -> {
				log.warn("Режиссёр с id = {} не найден при обновлении", director.getId());
				return new NotFoundException("Режиссёр с id = " + director.getId() + " не найден");
			});

		log.info("Режиссёр успешно обновлён: {}", updatedDirector);
		return directorMapper.mapToDirectorDto(updatedDirector);
	}

	public void deleteDirector(Integer id) {
		if (!directorStorage.deleteDirector(id)) {
			throw new NotFoundException("Режиссёр с id = " + id + " не найден");
		}
		log.info("Удалён режиссёр с id = {}", id);
	}
}