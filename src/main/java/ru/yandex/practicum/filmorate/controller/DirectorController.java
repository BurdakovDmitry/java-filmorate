package ru.yandex.practicum.filmorate.controller;

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
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Set;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Validated
public class DirectorController {
	private final DirectorService directorService;

	@GetMapping
	public Set<DirectorDto> getAllDirectors() {
		return directorService.getDirectors();
	}

	@GetMapping("/{id}")
	public DirectorDto getDirectorById(@PathVariable Long id) {
		return directorService.getDirectorById(id);
	}

	@PostMapping
	public DirectorDto createDirector(@RequestBody DirectorDto directorDto) {
		return directorService.createDirector(directorDto);
	}

	@PutMapping
	public DirectorDto updateDirector(@RequestBody DirectorDto directorDto) {
		return directorService.updateDirector(directorDto);
	}

	@DeleteMapping("/{id}")
	public void deleteDirector(@PathVariable Long id) {
		directorService.deleteDirector(id);
	}
}
