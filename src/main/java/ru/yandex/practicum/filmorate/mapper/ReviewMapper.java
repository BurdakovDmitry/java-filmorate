package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.review.Review;

import java.time.LocalDate;

public class ReviewMapper {
    public static Review fromReviewDto(ReviewDto dto) {
        var re = new Review();
        re.setId(dto.id());
        re.setContent(dto.content());
        re.setPositive(dto.isPositive());
        re.setUserId(dto.userId());
        re.setFilmId(dto.filmId());
        re.setCreatedAt(LocalDate.now());
        re.setUseful(0);

        return re;
    }
}
