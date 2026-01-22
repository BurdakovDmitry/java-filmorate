package ru.yandex.practicum.filmorate.model.review;

import lombok.Data;

@Data
public class ReviewLike {
    private Long reviewId;
    private Long userId;
    private Boolean isLike;
}
