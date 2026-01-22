package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReviewDto(
        @JsonProperty("reviewId")
        Long id,
        String content,
        @JsonProperty("isPositive")
        Boolean isPositive,
        Long userId,
        Long filmId
) {
}
