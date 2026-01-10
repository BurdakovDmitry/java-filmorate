package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MpaDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Integer id,
        String name) {
}
