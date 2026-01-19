package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DirectorDto(
	@JsonProperty("id") Long id,
	@JsonProperty("name") String name
) {
}
