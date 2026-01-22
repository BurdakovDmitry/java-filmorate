package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class ReviewService {
	private final ReviewStorage reviewStorage;
	private final Validation validation;


	public ReviewService(Validation validation,
						 ReviewStorage reviewStorage) {
		this.validation = validation;
		this.reviewStorage = reviewStorage;
	}

	public Review createReview(Review review) {
		if (review.getFilmId() == null
			|| review.getUserId() == null) {
			throw new ValidationException("Failed to create review when filmId is " + review.getFilmId() +
				" userId is " + review.getUserId());
		}
		validation.filmById(review.getFilmId());
		validation.userById(review.getUserId());
		return reviewStorage.createReview(review);
	}

	public Review getReview(Long reviewId) {
		validation.reviewById(reviewId);

		return reviewStorage.getReview(reviewId).get();
	}

	public void deleteReview(Long id) {
		validation.reviewById(id);

		reviewStorage.deleteReview(id);
	}

	public Review updateReview(Review review) {
		review.setCreatedAt(LocalDate.now());
		return reviewStorage.updateReview(review);
	}

	public List<Review> getReviewsByFilmId(Long filmId, int limit) {
		validation.filmById(filmId);
		if (filmId == null) {
			return reviewStorage.getAllReviews(limit);
		}
		return reviewStorage.getReviewsByFilmId(filmId, limit);
	}
}
