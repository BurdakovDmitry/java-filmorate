package ru.yandex.practicum.filmorate.storage.friend;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({FriendDbStorage.class, FriendRowMapper.class, UserDbStorage.class, UserRowMapper.class,})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendDbStorageTest {
	private final FriendDbStorage friendStorage;
	private final UserDbStorage userStorage;
	private Long userId;
	private Long friendId;

	@BeforeEach
	public void createData() {
		User user = new User("user@email.ru", "Login", "Name",
			LocalDate.of(1995, 12, 12));
		User friend = new User("friend@email.ru", "LoginFriend", "NameFriend",
			LocalDate.of(1995, 12, 21));

		userId = userStorage.createUser(user).getId();
		friendId = userStorage.createUser(friend).getId();
		friendStorage.addFriends(userId, friendId, true);
	}

	@Test
	void getListFriends() {
		List<Friend> userFriends = friendStorage.getListFriends(userId);

		assertThat(userFriends)
			.isNotEmpty()
			.hasSize(1)
			.extracting(Friend::getFriendId)
			.containsExactly(friendId);
	}

	@Test
	void addFriends() {
		List<Friend> userFriends = friendStorage.getListFriends(userId);

		assertThat(userFriends)
			.hasSize(1)
			.extracting(Friend::getFriendId)
			.contains(friendId);

		List<Friend> friendFriends = friendStorage.getListFriends(friendId);

		assertThat(friendFriends).isEmpty();
	}

	@Test
	void deleteFriends() {
		friendStorage.deleteFriends(userId, friendId);

		List<Friend> userFriends = friendStorage.getListFriends(userId);

		assertThat(userFriends).isEmpty();
	}
}