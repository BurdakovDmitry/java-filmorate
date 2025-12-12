package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        Collection<User> users = userStorage.findAll();
        log.info("Получен список пользователей: {}", users);
        return users;
    }

    public User createUser(User user) {
        User newUser = userStorage.createUser(user);
        log.info("Добавлен новый пользователь: {}", user);
        return newUser;
    }

    public User updateUser(User user) {
        log.info("id пользователя: {}", user.getId());
        User updateUser = userStorage.updateUser(user);
        log.info("Обновлены данные пользователя: {}", user);
        return updateUser;
    }

    public void addFriends(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        userFriend.getFriends().add(userId);

        log.info("Пользователи с id: {} и {} - теперь друзья", userId, friendId);
    }

    public void deleteFriends(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        userFriend.getFriends().remove(userId);
        log.info("Пользователи с id: {} и {} - больше не друзья", userId, friendId);
    }

    public List<User> getListFriends(Long userId) {
        User user = userStorage.getUserById(userId);

        log.info("Запрос списка друзей у пользователя с id: {}", userId);

        return user.getFriends().stream()
                .mapToLong(Long::valueOf)
                .mapToObj(userStorage::getUserById)
                .toList();
    }

    public List<User> getMutualFriends(Long userId, Long otherId) {
        User user = userStorage.getUserById(userId);
        User userFriend = userStorage.getUserById(otherId);

        log.info("Запрос общих друзей у пользователей с id: {} и {}", userId, otherId);

        return user.getFriends().stream()
                .filter(userFriend.getFriends()::contains)
                .map(userStorage::getUserById)
                .toList();
    }
}
