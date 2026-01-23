package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
	Collection<User> findAll();

	User createUser(User user);

	User updateUser(User user);

	Optional<User> getUserById(Long id);

	Optional<User> getUserByEmail(String email);

    Optional<User> getUserByLogin(String login);

    List<Film> getRecommendations(Long id);

    void deleteUser(Long id);
}
