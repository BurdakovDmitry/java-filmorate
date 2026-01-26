package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseRepository implements UserStorage {
	private final RowMapper<User> mapper;
	private final RowMapper<Film> filmMapper;
	private static final String FIND_ALL_QUERY =
			"SELECT user_id, email, login, name, birthday FROM users";
	private static final String FIND_BY_ID_QUERY =
			"SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";
	private static final String FIND_BY_EMAIL_QUERY =
			"SELECT user_id, email, login, name, birthday FROM users WHERE email = ?";
	private static final String FIND_BY_LOGIN_QUERY =
			"SELECT user_id, email, login, name, birthday FROM users WHERE login = ?";
	private static final String INSERT_QUERY =
			"INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_QUERY =
			"UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
	private static final String DELETE_QUERY =
			"DELETE FROM users WHERE user_id = ?";
	private static final String FIND_RECOMMENDATIONS_QUERY = """
			SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name
			FROM films AS f
			JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id
			JOIN likes AS l ON f.film_id = l.film_id
			JOIN (
				SELECT l2.user_id
				FROM likes AS l1
			   	JOIN likes AS l2 ON l1.film_id = l2.film_id
			   	WHERE l1.user_id = ? AND l2.user_id <> ?
			   	GROUP BY l2.user_id
			   	ORDER BY COUNT(l1.film_id) DESC LIMIT 1
			   	) AS recommend ON l.user_id = recommend.user_id
			WHERE f.film_id NOT IN (SELECT film_id FROM likes WHERE user_id = ?)
			""";

	public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper, RowMapper<Film> filmMapper) {
		super(jdbc);
		this.mapper = mapper;
		this.filmMapper = filmMapper;
	}

	@Override
	public List<User> findAll() {
		return jdbc.query(FIND_ALL_QUERY, mapper);
	}

	@Override
	public User createUser(User user) {
		long id = insert(INSERT_QUERY,
				user.getEmail(),
				user.getLogin(),
				user.getName(),
				user.getBirthday()
		);

		user.setId(id);
		return user;
	}

	@Override
	public User updateUser(User user) {
		update(UPDATE_QUERY,
				user.getEmail(),
				user.getLogin(),
				user.getName(),
				user.getBirthday(),
				user.getId()
		);

		return user;
	}

	@Override
	public Optional<User> getUserById(Long id) {
		return jdbc.query(FIND_BY_ID_QUERY, mapper, id).stream().findFirst();
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		return jdbc.query(FIND_BY_EMAIL_QUERY, mapper, email).stream().findFirst();
	}

	@Override
	public Optional<User> getUserByLogin(String login) {
		return jdbc.query(FIND_BY_LOGIN_QUERY, mapper, login).stream().findFirst();
	}

	@Override
	public List<Film> getRecommendations(Long userId) {
		return jdbc.query(FIND_RECOMMENDATIONS_QUERY, filmMapper, userId, userId, userId);
	}

	@Override
	public void deleteUser(Long userId) {
		update(DELETE_QUERY, userId);
	}
}
