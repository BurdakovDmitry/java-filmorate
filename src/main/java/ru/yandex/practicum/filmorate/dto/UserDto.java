package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;


public record UserDto(
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	long id,
	String email,
	String login,
	String name,
	LocalDate birthday) {
}
