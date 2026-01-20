package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.practicum.filmorate.model.review.Review;

import java.time.LocalDate;

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
