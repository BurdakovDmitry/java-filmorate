package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.Set;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Validated
public class DirectorController {
	private final DirectorService directorService;
	private final DirectorMapper mapper;
	private final Validation validation;

	@GetMapping
	public Set<DirectorDto> getAllDirectors() {
		return directorService.getDirectors();
	}

	@GetMapping("/{id}")
	public DirectorDto getDirectorById(@PathVariable Long id) {
		return directorService.getDirectorById(id);
	}

	@PostMapping
	public DirectorDto createDirector(@Valid @RequestBody DirectorDto directorDto) {
		validation.validateDirector(mapper.mapToDirector(directorDto));
		return directorService.createDirector(directorDto);
	}

	@PutMapping
	public DirectorDto updateDirector(@Valid @RequestBody DirectorDto directorDto) {
		validation.validateDirector(mapper.mapToDirector(directorDto));
		return directorService.updateDirector(directorDto);
	}

	@DeleteMapping("/{id}")
	public void deleteDirector(@PathVariable Long id) {
		directorService.deleteDirector(id);
	}
}
