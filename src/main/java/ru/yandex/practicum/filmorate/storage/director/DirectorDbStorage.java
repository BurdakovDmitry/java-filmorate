package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DirectorDbStorage extends BaseRepository implements DirectorStorage {
	private static final String CREATE_DIRECTOR =
		"INSERT INTO directors (name) VALUES (?)";
	private static final String FIND_ALL_QUERY =
		"SELECT director_id, name FROM directors ORDER BY director_id";
	private static final String FIND_BY_ID_QUERY =
		"SELECT director_id, name FROM directors WHERE director_id = ?";
	private static final String UPDATE_QUERY =
		"UPDATE directors SET name = ? WHERE director_id = ?";
	private static final String DELETE_QUERY =
		"DELETE FROM directors WHERE director_id = ?";
	private static final String FIND_BY_FILM_ID_QUERY = """
		SELECT d.director_id, d.name
		FROM directors AS d
		JOIN film_director AS fd ON d.director_id = fd.director_id
		WHERE fd.film_id = ?
		ORDER BY d.director_id
		""";

	private static final String DELETE_BY_FILM_ID_QUERY = """
		DELETE FROM film_director
		WHERE film_id = ?
		""";

	private static final String INSERT_TO_FILM_DIRECTOR = """
		INSERT INTO film_director (film_id, director_id)
		VALUES (?, ?)
		""";

	private static final String FIND_DIRECTORS_BY_FILM_IDS = """
		SELECT fd.film_id, d.director_id, d.name
		FROM directors d
		JOIN film_director fd ON d.director_id = fd.director_id
		WHERE fd.film_id IN (%s)
		ORDER BY fd.film_id, d.director_id
		""";

	private final DirectorRowMapper directorRowMapper = new DirectorRowMapper();

	public DirectorDbStorage(JdbcTemplate jdbc) {
		super(jdbc);
	}

	@Override
	public Set<Director> getDirectors() {
		return new LinkedHashSet<>(jdbc.query(FIND_ALL_QUERY, directorRowMapper));
	}

	@Override
	public Optional<Director> getDirectorById(Long id) {
		return jdbc.query(FIND_BY_ID_QUERY, directorRowMapper, id).stream().findFirst();
	}

	@Override
	public Director createDirector(Director director) {
		long id = insert(CREATE_DIRECTOR, director.getName());
		director.setId(id);
		log.info("Создан режиссёр: {}", director);
		return director;
	}

	@Override
	public Optional<Director> updateDirector(Director director) {
		int rows = jdbc.update(UPDATE_QUERY, director.getName(), director.getId());
		if (rows == 0) {
			log.warn("Режиссёр с id = {} не найден при обновлении", director.getId());
			return Optional.empty();
		}
		log.info("Обновлён режиссёр с id = {}", director.getId());
		return getDirectorById(director.getId());
	}

	@Override
	public boolean deleteDirector(Long id) {
		int rows = jdbc.update(DELETE_QUERY, id);
		if (rows > 0) {
			log.info("Удалён режиссёр с id = {}", id);
			return true;
		}
		log.warn("Режиссёр с id = {} не найден при удалении", id);
		return false;
	}

	@Override
	public void addDirectors(Long filmId, Set<Director> directors) {
		for (Director director : directors) {
			Long directorId = director.getId();
			if (directorId != null) {
				jdbc.update(INSERT_TO_FILM_DIRECTOR, filmId, directorId);
			}
		}
	}

	@Override
	public void deleteDirectors(Long filmId) {
		jdbc.update(DELETE_BY_FILM_ID_QUERY, filmId);
	}

	@Override
	public void updateFilmDirectors(Long filmId, Set<Director> directors) {
		deleteDirectors(filmId);
		addDirectors(filmId, directors);
	}

	@Override
	public Set<Director> getDirectorsByFilm(Long filmId) {
		return new LinkedHashSet<>(jdbc.query(FIND_BY_FILM_ID_QUERY, directorRowMapper, filmId));
	}

	@Override
	public void getDirectorsForFilms(List<Film> films) {
		if (films.isEmpty()) return;

		List<Long> filmIds = films.stream()
			.map(Film::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		if (filmIds.isEmpty()) return;

		String placeholders = String.join(", ", Collections.nCopies(filmIds.size(), "?"));
		String sql = String.format(FIND_DIRECTORS_BY_FILM_IDS, placeholders);

		Map<Long, Set<Director>> directorsByFilmId = new HashMap<>();

		jdbc.query(connection -> {
			var ps = connection.prepareStatement(sql);
			for (int i = 0; i < filmIds.size(); i++) {
				ps.setLong(i + 1, filmIds.get(i));
			}
			return ps;
		}, rs -> {
			Long filmId = rs.getLong("film_id");
			Long directorId = rs.getLong("director_id");

			if (rs.wasNull()) {
				log.warn("Получен NULL для director_id в результате запроса");
				return;
			}

			String name = rs.getString("name");

			Director director = new Director(directorId, name);
			directorsByFilmId
				.computeIfAbsent(filmId, k -> new LinkedHashSet<>())
				.add(director);
		});

		for (Film film : films) {
			if (film.getId() != null) {
				Set<Director> directors = directorsByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>());
				film.setDirectors(directors);
			}
		}

		log.info("Загружено режиссёров по фильмам: {}", directorsByFilmId);
	}
}