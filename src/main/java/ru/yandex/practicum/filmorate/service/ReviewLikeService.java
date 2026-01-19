package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.review.like.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.validation.Validation;

@Slf4j
@Service
public class ReviewLikeService {
    private final ReviewLikeStorage reviewLikeStorage;
    private final Validation validation;

    @Autowired
    public ReviewLikeService(Validation validation,
                             ReviewLikeStorage reviewLikeStorage) {
        this.validation = validation;
        this.reviewLikeStorage = reviewLikeStorage;
    }

    public void like(Long reviewId, Long userId) {
        validation.reviewById(reviewId);

        reviewLikeStorage.like(reviewId, userId, true);
    }

    public void disLike(Long reviewId, Long userId) {
        validation.reviewById(reviewId);

        reviewLikeStorage.like(reviewId, userId, false);
    }

    public void removeLike(Long reviewId, Long userId) {
        validation.reviewById(reviewId);

        reviewLikeStorage.removeLike(reviewId, userId, true);
    }

    public void removeDisLike(Long reviewId, Long userId) {
        validation.reviewById(reviewId);

        reviewLikeStorage.removeLike(reviewId, userId, false);
    }
}
