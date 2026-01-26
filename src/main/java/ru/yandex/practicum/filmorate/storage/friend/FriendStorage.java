package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;

public interface FriendStorage {
	void addFriends(Long userId, Long friendId, boolean status);

	void deleteFriends(Long userId, Long friendId);

	List<Friend> getListFriends(Long userId);
}
