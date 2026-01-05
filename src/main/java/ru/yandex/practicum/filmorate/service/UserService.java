package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final Validation validation;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendStorage friendStorage,
                       Validation validation) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
        this.validation = validation;
    }

    public List<UserDto> findAll() {
        List<UserDto> users = userStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();

        log.info("Получен список пользователей: {}", users);
        return users;
    }

    public UserDto getUserById(Long id) {
        UserDto userDto = userStorage.getUserById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));

        log.info("Получен пользователь: {}", userDto);
        return userDto;
    }

    public UserDto createUser(User user) {
        validation.uniqueEmailCreateUser(user.getEmail());
        validation.uniqueLoginCreateUser(user.getLogin());

        User newUser = userStorage.createUser(user);

        log.info("Добавлен новый пользователь: {}", newUser);
        return UserMapper.mapToUserDto(newUser);
    }

    public UserDto updateUser(User user) {
        validation.userById(user.getId());
        validation.uniqueEmailUpdateUser(user.getEmail(), user.getId());
        validation.uniqueLoginUpdateUser(user.getLogin(), user.getId());

        User updateUser = userStorage.updateUser(user);

        log.info("Обновлены данные пользователя: {}", user);
        return UserMapper.mapToUserDto(updateUser);
    }

    public void addFriends(Long userId, Long friendId) {
        validation.userById(userId);
        validation.userById(friendId);

        friendStorage.addFriends(userId, friendId, true);
        log.info("Пользователь с id: {} добавил к себе друга с id: {}", userId, friendId);
    }

    public void deleteFriends(Long userId, Long friendId) {
        validation.userById(userId);
        validation.userById(friendId);

        friendStorage.deleteFriends(userId, friendId);
        log.info("Пользователь с id: {} удалил из друзей пользователя с id: {}", userId, friendId);
    }

    public List<UserDto> getListFriends(Long userId) {
        validation.userById(userId);

        log.info("Запрос списка друзей у пользователя с id: {}", userId);

        return friendStorage.getListFriends(userId).stream()
                .map(Friend::getFriendId)
                .map(userStorage::getUserById)
                .flatMap(Optional::stream)
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> getMutualFriends(Long userId, Long otherId) {
        validation.userById(userId);
        validation.userById(otherId);
        List<UserDto> userFriends = getListFriends(userId);
        List<UserDto> otherFriends = getListFriends(otherId);

        log.info("Запрос общих друзей у пользователей с id: {} и {}", userId, otherId);

        return otherFriends.stream()
                .filter(userFriends::contains)
                .toList();
    }
}
