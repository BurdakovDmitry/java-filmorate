package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.service.ReviewLikeService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
	private final ReviewService reviewService;
	private final ReviewLikeService reviewLikeService;
	private final ReviewMapper reviewMapper;

	@PostMapping
	public ReviewDto add(@RequestBody ReviewDto review) {
		return reviewService.createReview(reviewMapper.mapToReview(review));
	}

	@PutMapping
	public ReviewDto update(@RequestBody ReviewDto review) {
		return reviewService.updateReview(reviewMapper.mapToReview(review));
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		reviewService.deleteReview(id);
	}

	@GetMapping("/{id}")
	public ReviewDto get(@PathVariable(required = false) Long id) {
		return reviewService.getReview(id);
	}

	@PutMapping("/{id}/like/{userId}")
	public void like(@PathVariable Long id,
					 @PathVariable Long userId) {
		reviewLikeService.like(id, userId);
	}

	@GetMapping
	public List<ReviewDto> getByFilmId(@RequestParam(required = false) Long filmId,
									@RequestParam(defaultValue = "10") int count) {
		return reviewService.getReviewsByFilmId(filmId, count);
	}

	@PutMapping("/{id}/dislike/{userId}")
	public void disLike(@PathVariable Long id,
						@PathVariable Long userId) {
		reviewLikeService.disLike(id, userId);
	}

	@DeleteMapping("/{id}/like/{userId}")
	public void removeLike(@PathVariable Long id,
						   @PathVariable Long userId) {
		reviewLikeService.removeLike(id, userId);
	}

	@DeleteMapping("/{id}/dislike/{userId}")
	public void removeDisLike(@PathVariable Long id,
							  @PathVariable Long userId) {
		reviewLikeService.removeDisLike(id, userId);
	}
}
