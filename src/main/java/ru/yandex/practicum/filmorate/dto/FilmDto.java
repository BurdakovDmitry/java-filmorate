package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Set;

public record FilmDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        long id,
        String name,
        String description,
        LocalDate releaseDate,
        int duration,
        MpaDto mpa,
        Set<GenreDto> genres) {
}
