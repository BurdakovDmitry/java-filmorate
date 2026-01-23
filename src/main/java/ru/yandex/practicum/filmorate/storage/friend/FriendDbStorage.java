package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
	private static final String INSERT_QUERY = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
	private static final String DELETE_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
	private static final String FIND_BY_ID_QUERY = "SELECT * FROM friends WHERE user_id = ?";
	private final JdbcTemplate jdbc;
	private final RowMapper<Friend> mapper;

	@Override
	public void addFriends(Long userId, Long friendId, boolean status) {
		jdbc.update(INSERT_QUERY, userId, friendId, status);
	}

	@Override
	public void deleteFriends(Long userId, Long friendId) {
		jdbc.update(DELETE_QUERY, userId, friendId);
	}

	@Override
	public List<Friend> getListFriends(Long userId) {
		return jdbc.query(FIND_BY_ID_QUERY, mapper, userId);
	}
}
