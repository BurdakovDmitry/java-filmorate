package ru.yandex.practicum.filmorate.storage.review.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.review.ReviewLike;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Optional;

@Slf4j
@Component
public class ReviewLikeDbStorage extends BaseRepository implements ReviewLikeStorage {
	private static final String LIKE_QUERY =
		"UPDATE reviews SET useful = useful + ? WHERE id = ?";
	private static final String UPDATE_QUERY =
		"UPDATE review_like SET is_like = ? WHERE review_id = ? AND user_id = ?";
	private static final String INSERT_QUERY =
		"INSERT INTO review_like (review_id, user_id, is_like) VALUES (?, ?, ?)";
	private static final String DELETE_QUERY =
		"DELETE FROM review_like WHERE review_id = ? AND user_id = ?";
	private static final String FIND_QUERY =
		"SELECT review_id, user_id, is_like FROM review_like WHERE review_id = ? AND user_id = ?";
	private static final String DISLIKE_QUERY =
		"UPDATE reviews SET useful = useful - ? WHERE id = ?";

	public ReviewLikeDbStorage(JdbcTemplate jdbc) {
		super(jdbc);
	}

	public Optional<ReviewLike> getLike(Long reviewId, Long userId) {
		return jdbc.query(
				FIND_QUERY,
				new BeanPropertyRowMapper<>(ReviewLike.class),
				reviewId,
				userId
		).stream().findFirst();
	}


	@Override
	public void like(Long reviewId, Long userId, boolean isLike) {
		var like = getLike(reviewId, userId);
		if (like.isPresent()) {
			if (like.get().getIsLike() == isLike) {
				return;
			}
			super.update(UPDATE_QUERY, isLike, reviewId, userId);
			super.update(isLike ? LIKE_QUERY : DISLIKE_QUERY, 2, reviewId);
			return;
		}
		super.update(INSERT_QUERY, reviewId, userId, isLike);
		super.update(isLike ? LIKE_QUERY : DISLIKE_QUERY, 1, reviewId);
	}

	@Override
	public void removeLike(Long reviewId, Long userId, boolean isLike) {
		super.update(DELETE_QUERY, reviewId, userId);
		super.update(isLike ? DISLIKE_QUERY : LIKE_QUERY, 1, reviewId);
	}
}
