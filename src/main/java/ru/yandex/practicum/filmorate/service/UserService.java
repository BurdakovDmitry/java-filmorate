package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final Validation validation;
    private final UserMapper userMapper;
    private final FilmMapper filmMapper;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final EventService eventService;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FriendStorage friendStorage,
                       Validation validation,
                       UserMapper userMapper,
                       FilmMapper filmMapper,
                       GenreStorage genreStorage,
                       DirectorStorage directorStorage,
                       EventService eventService) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
        this.validation = validation;
        this.userMapper = userMapper;
        this.filmMapper = filmMapper;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
        this.eventService = eventService;
    }

	public List<UserDto> findAll() {
		List<UserDto> users = userStorage.findAll()
			.stream()
			.map(userMapper::mapToUserDto)
			.toList();

		log.info("Получен список пользователей: {}", users);
		return users;
	}

	public UserDto getUserById(Long id) {
		UserDto userDto = userStorage.getUserById(id)
			.map(userMapper::mapToUserDto)
			.orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));

		log.info("Получен пользователь: {}", userDto);
		return userDto;
	}

	public UserDto createUser(User user) {
		validation.uniqueEmailCreateUser(user.getEmail());
		validation.uniqueLoginCreateUser(user.getLogin());

		User newUser = userStorage.createUser(user);

		log.info("Добавлен новый пользователь: {}", newUser);
		return userMapper.mapToUserDto(newUser);
	}

	public UserDto updateUser(User user) {
		validation.userById(user.getId());
		validation.uniqueEmailUpdateUser(user.getEmail(), user.getId());
		validation.uniqueLoginUpdateUser(user.getLogin(), user.getId());

		User updateUser = userStorage.updateUser(user);

		log.info("Обновлены данные пользователя: {}", user);
		return userMapper.mapToUserDto(updateUser);
	}

    public void deleteUser(Long userId) {
        validation.userById(userId);

        userStorage.deleteUser(userId);
        log.info("Пользователь с id = {} успешно удален", userId);
    }

    public void addFriends(Long userId, Long friendId) {
        validation.userById(userId);
        validation.userById(friendId);

        friendStorage.addFriends(userId, friendId, true);
        var event = new Event(userId, friendId, EventType.FRIEND, OperationType.ADD, Instant.now());
        eventService.send(event);
        log.info("Пользователь с id: {} добавил к себе друга с id: {}", userId, friendId);
    }

	public void deleteFriends(Long userId, Long friendId) {
		validation.userById(userId);
		validation.userById(friendId);

        friendStorage.deleteFriends(userId, friendId);
        var event = new Event(userId, friendId, EventType.FRIEND, OperationType.REMOVE, Instant.now());
        eventService.send(event);
        log.info("Пользователь с id: {} удалил из друзей пользователя с id: {}", userId, friendId);
    }

	public List<UserDto> getListFriends(Long userId) {
		validation.userById(userId);

		log.info("Запрос списка друзей у пользователя с id: {}", userId);

		return friendStorage.getListFriends(userId).stream()
			.map(Friend::getFriendId)
			.map(userStorage::getUserById)
			.flatMap(Optional::stream)
			.map(userMapper::mapToUserDto)
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

    public List<FilmDto> getRecommendations(Long userId) {
        validation.userById(userId);

        List<Film> films = userStorage.getRecommendations(userId);

        genreStorage.getGenresForFilms(films);
        directorStorage.getDirectorsForFilms(films);

        log.info("Запрос на рекомендацию фильмов для пользователя с id: {}", userId);

        return films.stream()
                .map(filmMapper::mapToFilmDto)
                .toList();
    }
}
