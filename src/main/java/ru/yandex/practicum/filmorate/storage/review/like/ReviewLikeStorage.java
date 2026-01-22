package ru.yandex.practicum.filmorate.storage.review.like;

import ru.yandex.practicum.filmorate.model.review.ReviewLike;

import java.util.Optional;

public interface ReviewLikeStorage {
	void like(Long reviewId, Long userId, boolean isLike);


	void removeLike(Long reviewId, Long userId, boolean isLike);

	Optional<ReviewLike> getLike(Long reviewId, Long userId);
}
