package ru.yandex.practicum.filmorate.storage.review;


import ru.yandex.practicum.filmorate.model.review.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

	Review createReview(Review review);

	Optional<Review> getReview(Long reviewId);

	void deleteReview(Long id);

	Review updateReview(Review review);

	List<Review> getReviewsByFilmId(Long filmid, int limit);

	List<Review> getAllReviews(int limit);
}
