package ru.yandex.practicum.filmorate.storage.review.like;

public interface ReviewLikeStorage {
    void like(Long reviewId, Long userId, boolean isLike);


    void removeLike(Long reviewId, Long userId, boolean isLike);


}
