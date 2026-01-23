package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
	private final Map<Long, User> users = new HashMap<>();
	private final Map<String, User> uniqueEmail = new HashMap<>();
	private final Map<String, User> uniqueLogin = new HashMap<>();
	private Long id;

	@Override
	public Collection<User> findAll() {
		return users.values();
	}

	@Override
	public User createUser(User user) {
		if (user.getName() == null || user.getName().isBlank()) {
			user.setName(user.getLogin());
			log.info("Имя пользователя = логин {}", user.getLogin());
		}

		user.setId(getNextId());

		log.info("Пользователю {} присвоился id={}.", user.getLogin(), user.getId());

		users.put(user.getId(), user);
		uniqueEmail.put(user.getEmail(), user);
		uniqueLogin.put(user.getLogin(), user);
		return user;
	}

	@Override
	public User updateUser(User user) {
		if (users.containsKey(user.getId())) {
			User oldUser = users.get(user.getId());

			if (!user.getEmail().equals(oldUser.getEmail())) {
				if (uniqueEmail.containsKey(user.getEmail())) {
					log.warn("Валидация по email не пройдена у {}", user);
					throw new DuplicatedDataException("Этот имейл уже используется");
				}

				log.info("Был имейл = {}", oldUser.getEmail());
				oldUser.setEmail(user.getEmail());
				log.info("Присвоен новый имейл = {}", user.getEmail());
			}

			if (!user.getLogin().equals(oldUser.getLogin())) {
				if (uniqueLogin.containsKey(user.getLogin())) {
					log.warn("Валидация по login не пройдена у {}", user);
					throw new DuplicatedDataException("Этот логин уже используется");
				}

				log.info("Был логин = {}", oldUser.getLogin());
				oldUser.setLogin(user.getLogin());
				log.info("Присвоен новый логин = {}", user.getLogin());
			}

			if (user.getName() == null || user.getName().isBlank()) {
				oldUser.setName(user.getLogin());
				log.info("Так как имя пользователя не указано, то имя = логин {}", user.getLogin());
			}

			if (!user.getName().equals(oldUser.getName())) {
				log.info("Было имя = {}", oldUser.getName());
				oldUser.setName(user.getName());
				log.info("Присвоено новое имя = {}", user.getName());
			}

			if (!user.getBirthday().equals(oldUser.getBirthday())) {
				log.info("Старая дата рождения = {}", oldUser.getBirthday());
				oldUser.setBirthday(user.getBirthday());
				log.info("Новая дата рождения = {}", user.getBirthday());
			}

			users.put(oldUser.getId(), oldUser);
			uniqueEmail.put(oldUser.getEmail(), oldUser);
			uniqueLogin.put(oldUser.getLogin(), oldUser);
			return oldUser;
		}

		log.error("Ошибка поиска пользователя - {}", user);
		throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
	}

	@Override
	public Optional<User> getUserById(Long userId) {
		if (users.get(userId) == null) {
			log.error("Ошибка поиска фильма");
			throw new NotFoundException("Фильм с id = " + userId + " не найден");
		}

		return Optional.of(users.get(userId));
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		return Optional.empty();
	}

	@Override
	public Optional<User> getUserByLogin(String login) {
		return Optional.empty();
	}

	private long getNextId() {
		if (users.isEmpty()) {
			id = 1L;
			return id;
		} else {
			return ++id;
		}
	}

    @Override
    public void deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            log.error("Ошибка поиска пользователя для удаления - {}", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        User removedUser = users.remove(userId);
        uniqueEmail.remove(removedUser.getEmail());
        uniqueLogin.remove(removedUser.getLogin());
        log.info("Пользователь удален: {}", removedUser);
    }
}
