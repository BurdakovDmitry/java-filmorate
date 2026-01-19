package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
	private static final String INSERT_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
	private static final String DELETE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
	private final JdbcTemplate jdbc;

	@Override
	public void addLike(Long filmId, Long userId) {
		jdbc.update(INSERT_QUERY, filmId, userId);
	}

	@Override
	public void deleteLike(Long filmId, Long userId) {
		jdbc.update(DELETE_QUERY, filmId, userId);
	}
}
