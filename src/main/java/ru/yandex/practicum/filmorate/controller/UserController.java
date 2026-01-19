package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final Validation validation;

	@GetMapping
	public List<UserDto> findAll() {
		return userService.findAll();
	}

	@GetMapping("/{id}")
	public UserDto getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto createUser(@RequestBody User user) {
		validation.validationUser(user);
		return userService.createUser(user);
	}

	@PutMapping
	public UserDto updateUser(@RequestBody User user) {
		if (user.getId() == null) {
			log.warn("Валидация по id не пройдена для {}", user);
			throw new ValidationException("Id должен быть указан");
		}

		validation.validationUser(user);
		return userService.updateUser(user);
	}

	@PutMapping("/{id}/friends/{friendId}")
	public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
		userService.addFriends(id, friendId);
	}

	@DeleteMapping("/{id}/friends/{friendId}")
	public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
		userService.deleteFriends(id, friendId);
	}

	@GetMapping("/{id}/friends")
	public List<UserDto> getListFriends(@PathVariable Long id) {
		return userService.getListFriends(id);
	}

	@GetMapping("/{id}/friends/common/{otherId}")
	public List<UserDto> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
		return userService.getMutualFriends(id, otherId);
	}
}
