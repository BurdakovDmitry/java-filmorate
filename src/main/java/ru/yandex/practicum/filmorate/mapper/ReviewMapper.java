package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.review.Review;

import java.time.LocalDate;

@Mapper(componentModel = "spring", imports = {LocalDate.class})
public interface ReviewMapper {
	@Mapping(target = "createdAt", expression = "java(LocalDate.now())")
	@Mapping(target = "useful", constant = "0")
	Review mapToReview(ReviewDto review);

	ReviewDto mapToReviewDto(Review review);
}
