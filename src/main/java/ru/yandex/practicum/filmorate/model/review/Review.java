package ru.yandex.practicum.filmorate.model.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Review {
    @JsonProperty("reviewId")
    private Long id;
    private String content;
    private LocalDate createdAt;
    @JsonProperty("isPositive")
    private boolean isPositive;
    private int useful;
    private Long userId;
    private Long filmId;
}
