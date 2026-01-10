package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GenreDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Integer id,
        String name) {
}
