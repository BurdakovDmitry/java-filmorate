package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class FilmDbStorage extends BaseRepository implements FilmStorage {
	private static final String FIND_ALL_QUERY =
		"SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name " +
			"FROM films AS f " +
			"LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id ";
	private static final String FIND_BY_ID_QUERY =
		"SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name " +
			"FROM films AS f " +
			"LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id " +
			"WHERE f.film_id = ?";
	private static final String INSERT_QUERY =
		"INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
	private static final String UPDATE_QUERY =
		"UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
	private static final String FIND_POPULAR_QUERY =
		"SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name " +
			"FROM films AS f " +
			"LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id " +
			"LEFT JOIN likes AS l ON f.film_id = l.film_id " +
			"GROUP BY f.film_id, fm.mpa_name " +
			"ORDER BY COUNT(l.user_id) DESC " +
			"LIMIT ?";
	private static final String FIND_FILMS_BY_DIRECTOR =
		"SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
			"f.mpa_id, m.mpa_name " +
			"FROM films f " +
			"JOIN film_director fd ON f.film_id = fd.film_id " +
			"JOIN directors d ON fd.director_id = d.director_id " +
			"JOIN film_mpa m ON f.mpa_id = m.mpa_id " +
			"WHERE d.director_id = ? " +
			"ORDER BY f.release_date";
	private static final String FIND_FILMS_BY_DIRECTOR_SORTED_BY_YEAR =
		"SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
			"f.mpa_id, m.mpa_name " +
			"FROM films f " +
			"JOIN film_director fd ON f.film_id = fd.film_id " +
			"JOIN directors d ON fd.director_id = d.director_id " +
			"LEFT JOIN film_mpa m ON f.mpa_id = m.mpa_id " +
			"WHERE d.director_id = ? " +
			"ORDER BY f.release_date ASC";
	private static final String FIND_FILMS_BY_DIRECTOR_SORTED_BY_LIKES = """
		SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name
		FROM films f
		JOIN film_director fd ON f.film_id = fd.film_id
		JOIN directors d ON fd.director_id = d.director_id
		LEFT JOIN film_mpa m ON f.mpa_id = m.mpa_id
		LEFT JOIN likes l ON f.film_id = l.film_id
		WHERE d.director_id = ?
		GROUP BY f.film_id, m.mpa_name
		ORDER BY COUNT(l.user_id) DESC, f.release_date
		""";
	private final RowMapper<Film> mapper;

	public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
		super(jdbc);
		this.mapper = mapper;
	}

	@Override
	public List<Film> findAll() {
		return jdbc.query(FIND_ALL_QUERY, mapper);
	}

	@Override
	public Optional<Film> getFilmById(Long id) {
		return jdbc.query(FIND_BY_ID_QUERY, mapper, id).stream().findFirst();
	}

	@Override
	public Film createFilm(Film film) {
		long id = insert(INSERT_QUERY,
			film.getName(),
			film.getDescription(),
			film.getReleaseDate(),
			film.getDuration(),
			(film.getMpa() != null) ? film.getMpa().getId() : null
		);

		film.setId(id);
		return film;
	}

	@Override
	public Film updateFilm(Film film) {
		update(UPDATE_QUERY,
			film.getName(),
			film.getDescription(),
			film.getReleaseDate(),
			film.getDuration(),
			(film.getMpa() != null) ? film.getMpa().getId() : null,
			film.getId()
		);

		return film;
	}

	@Override
	public List<Film> getPopularFilms(int count) {
		return jdbc.query(FIND_POPULAR_QUERY, mapper, count);
	}


	@Override
	public List<Film> getFilmsByDirector(Integer directorId) {
		log.info("Запрос фильмов для режиссёра с id = {}", directorId);

		Integer directorExists = jdbc.queryForObject(
			"SELECT COUNT(*) FROM directors WHERE director_id = ?",
			Integer.class,
			directorId
		);
		log.info("Режиссёр с id = {} существует: {}", directorId, directorExists > 0);

		List<Long> filmIds = jdbc.queryForList(
			"SELECT film_id FROM film_director WHERE director_id = ?",
			Long.class,
			directorId
		);
		log.info("Найдено связей в film_director для director_id {}: {}", directorId, filmIds);

		if (!filmIds.isEmpty()) {
			String inClause = String.join(",", filmIds.stream().map(i -> "?").toList());
			List<String> filmNames = jdbc.queryForList(
				"SELECT name FROM films WHERE film_id IN (" + inClause + ")",
				String.class,
				filmIds.toArray()
			);
			log.info("Фильмы, найденные по id: {}", filmNames);
		}

		List<Film> films = jdbc.query(FIND_FILMS_BY_DIRECTOR, mapper, directorId);

		log.info("Итоговое количество фильмов, возвращённых запросом: {}", films.size());
		if (films.isEmpty()) {
			log.warn("Запрос вернул пустой список.");
		} else {
			films.forEach(film ->
				log.info("Фильм: id={}, name='{}', mpa_id={}, releaseDate={}",
					film.getId(), film.getName(), film.getMpa() != null ? film.getMpa().getId() : null,
					film.getReleaseDate())
			);
		}

		return films;
	}

	@Override
	public List<Film> getFilmsByDirectorSortedByYear(Integer directorId) {
		log.info("Запрос фильмов для режиссёра {} с сортировкой по year", directorId);
		List<Film> films = jdbc.query(FIND_FILMS_BY_DIRECTOR_SORTED_BY_YEAR, mapper, directorId);
		log.info("Найдено {} фильмов, отсортированных по дате: {}", films.size(),
			films.stream().map(f -> f.getId() + "=" + f.getReleaseDate()).toList());
		return films;
	}

	@Override
	public List<Film> getFilmsByDirectorSortedByLikes(Integer directorId) {
		return jdbc.query(FIND_FILMS_BY_DIRECTOR_SORTED_BY_LIKES, mapper, directorId);
	}
}

