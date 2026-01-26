package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Set;

public record FilmDto(
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	Long id,
	String name,
	String description,
	LocalDate releaseDate,
	Integer duration,
	MpaDto mpa,
	Set<GenreDto> genres,
	Set<DirectorDto> directors) {
}
