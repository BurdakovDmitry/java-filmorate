package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.review.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final Validation validation;
    private final EventService eventService;
    private final ReviewMapper reviewMapper;


    public ReviewService(Validation validation,
                         ReviewStorage reviewStorage,
                         EventService eventService, ReviewMapper mapper) {
        this.validation = validation;
        this.reviewStorage = reviewStorage;
        this.eventService = eventService;
        this.reviewMapper = mapper;
    }

    public ReviewDto createReview(Review review) {
        if (review.getFilmId() == null || review.getUserId() == null) {
            throw new ValidationException("Failed to create review when filmId is " + review.getFilmId() +
                    " userId is " + review.getUserId());
        }
        validation.filmById(review.getFilmId());
        validation.userById(review.getUserId());
        validation.validationReview(review);
        review = reviewStorage.createReview(review);

        var event = new Event();
        event.setEventType(EventType.REVIEW);
        event.setOperation(OperationType.ADD);
        event.setUserId(review.getUserId());
        event.setTimestamp(Instant.now());
        event.setEntityId(review.getId());
        eventService.send(event);

        return reviewMapper.mapToReviewDto(review);
    }

    public ReviewDto getReview(Long reviewId) {
        validation.reviewById(reviewId);

        return reviewStorage.getReview(reviewId)
                .map(reviewMapper::mapToReviewDto)
                .orElseThrow(() -> new NotFoundException("Review с id = " + reviewId + " не найден"));
    }

    public void deleteReview(Long id) {
        validation.reviewById(id);

        var userId = reviewMapper.mapToReview(getReview(id)).getUserId();
        reviewStorage.deleteReview(id);

        var event = new Event();
        event.setEventType(EventType.REVIEW);
        event.setOperation(OperationType.REMOVE);
        event.setUserId(userId);
        event.setTimestamp(Instant.now());
        event.setEntityId(id);
        eventService.send(event);
    }

    public ReviewDto updateReview(Review review) {
        validation.reviewById(review.getId());
        validation.validationReview(review);

        review = reviewStorage.updateReview(review);

        var event = new Event();
        event.setEventType(EventType.REVIEW);
        event.setOperation(OperationType.UPDATE);
        event.setUserId(review.getUserId());
        event.setTimestamp(Instant.now());
        event.setEntityId(review.getId());
        eventService.send(event);

        return reviewMapper.mapToReviewDto(review);
    }

    public List<ReviewDto> getReviewsByFilmId(Long filmId, int limit) {
        if (filmId == null) {
            return reviewStorage.getAllReviews(limit)
                    .stream()
                    .map(reviewMapper::mapToReviewDto)
                    .toList();
        }

        validation.filmById(filmId);

        return reviewStorage.getReviewsByFilmId(filmId, limit)
                .stream()
                .map(reviewMapper::mapToReviewDto)
                .toList();
    }
}
