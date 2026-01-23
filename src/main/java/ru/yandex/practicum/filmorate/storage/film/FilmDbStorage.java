package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseRepository implements FilmStorage {
	private static final String FIND_ALL_QUERY = """
		SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name
		FROM films AS f
		LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id
		""";
	private static final String FIND_BY_ID_QUERY = """
		SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name
		FROM films AS f
		LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id
		WHERE f.film_id = ?
		""";
	private static final String INSERT_QUERY = """
		INSERT INTO films (name, description, release_date, duration, mpa_id)
		VALUES (?, ?, ?, ?, ?)
		""";
	private static final String UPDATE_QUERY = """
		UPDATE films
		SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
		WHERE film_id = ?
		""";
    private static final String DELETE_QUERY =
        "DELETE FROM films WHERE film_id = ?";
	private static final String FIND_POPULAR_QUERY = """
		SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name
		FROM films AS f
		LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id
		LEFT JOIN likes AS l ON f.film_id = l.film_id
		GROUP BY f.film_id, fm.mpa_name
		ORDER BY COUNT(DISTINCT l.user_id) DESC
		LIMIT ?
		""";
	private static final String FIND_FILMS_BY_DIRECTOR = """
		SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name
		FROM films f
		LEFT JOIN film_director fd ON f.film_id = fd.film_id
		LEFT JOIN directors d ON fd.director_id = d.director_id
		LEFT JOIN film_mpa m ON f.mpa_id = m.mpa_id
		WHERE d.director_id = ?
		ORDER BY f.release_date
		""";
	private static final String FIND_FILMS_BY_DIRECTOR_SORTED_BY_YEAR = """
		SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name
		FROM films f
		LEFT JOIN film_director fd ON f.film_id = fd.film_id
		LEFT JOIN directors d ON fd.director_id = d.director_id
		LEFT JOIN film_mpa m ON f.mpa_id = m.mpa_id
		WHERE d.director_id = ?
		ORDER BY f.release_date ASC
		""";
	private static final String FIND_FILMS_BY_DIRECTOR_SORTED_BY_LIKES = """
		SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name
		FROM films f
		LEFT JOIN film_director fd ON f.film_id = fd.film_id
		LEFT JOIN directors d ON fd.director_id = d.director_id
		LEFT JOIN film_mpa m ON f.mpa_id = m.mpa_id
		LEFT JOIN likes l ON f.film_id = l.film_id
		WHERE d.director_id = ?
		GROUP BY f.film_id, m.mpa_name
		ORDER BY COUNT(DISTINCT l.user_id) DESC, f.release_date
		""";
	private static final String SEARCH_BY_TITLE = """
    SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name
    FROM films AS f
    LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id
    LEFT JOIN likes AS l ON f.film_id = l.film_id
    WHERE LOWER(f.name) LIKE ?
    GROUP BY f.film_id, fm.mpa_name
    ORDER BY COUNT(DISTINCT l.user_id) DESC, f.name ASC
    """;
	private static final String SEARCH_BY_DIRECTOR_ID = """
		SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name
		FROM films AS f
		LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id
		LEFT JOIN likes AS l ON f.film_id = l.film_id
		WHERE f.film_id IN (
		    SELECT fd.film_id FROM film_director fd WHERE fd.director_id = ?
		)
		GROUP BY f.film_id, fm.mpa_name
		ORDER BY COUNT(l.user_id) DESC, f.name ASC
		""";
	private static final String SEARCH_BY_TITLE_AND_DIRECTOR = """
		SELECT DISTINCT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name
		FROM films AS f
		LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id
		LEFT JOIN likes AS l ON f.film_id = l.film_id
		WHERE f.film_id IN (
		    SELECT fd.film_id FROM film_director fd WHERE fd.director_id = ?
		    )
		AND LOWER(f.name) LIKE ?
		GROUP BY f.film_id, fm.mpa_name
		ORDER BY COUNT(l.user_id) DESC, f.name ASC
		""";
	private static final String FIND_COMMON_QUERY = """
		SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name
		FROM films AS f
		JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id
		WHERE f.film_id IN (
		    SELECT l1.film_id
		    FROM likes AS l1
		    JOIN likes AS l2 ON l1.film_id = l2.film_id
		    WHERE l1.user_id = ? AND l2.user_id = ?
		)
		ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) DESC""";

	private final RowMapper<Film> mapper;
	private final GenreStorage genreStorage;

	public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, GenreStorage genreStorage) {
		super(jdbc);
		this.mapper = mapper;
		this.genreStorage = genreStorage;
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
		long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
			(film.getMpa() != null) ? film.getMpa().getId() : null);
		film.setId(id);
		return film;
	}

	@Override
	public Film updateFilm(Film film) {
		update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
			(film.getMpa() != null) ? film.getMpa().getId() : null, film.getId());
		return film;
	}

    @Override
    public void deleteFilm(Long filmId) {
        update(DELETE_QUERY, filmId);
    }

	@Override
	public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
		List<Object> params = new ArrayList<>();
		if (genreId == null && year == null) {
			return jdbc.query(FIND_POPULAR_QUERY, mapper, count);
		}

		StringBuilder queryBuilder = new StringBuilder(
			"SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name " +
				"FROM films AS f " + "LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id " +
				"LEFT JOIN likes AS l ON f.film_id = l.film_id ");
		if (genreId != null) {
			queryBuilder.append(" INNER JOIN film_genre AS fg ON f.film_id = fg.film_id ");
		}

		queryBuilder.append(" WHERE 1=1 ");
		if (genreId != null) {
			queryBuilder.append(" AND f.film_id IN (SELECT fg.film_id FROM film_genre fg WHERE fg.genre_id = ?) ");
			params.add(genreId);
		}

		if (year != null) {
			queryBuilder.append(" AND EXTRACT(YEAR FROM f.release_date) = ? ");
			params.add(year);
		}

		queryBuilder.append(" GROUP BY f.film_id, fm.mpa_name ");
		queryBuilder.append(" ORDER BY COUNT(l.user_id) DESC ");
		queryBuilder.append(" LIMIT ? ");
		params.add(count);

		List<Film> films = jdbc.query(queryBuilder.toString(), mapper, params.toArray());
		return films;
	}

	@Override
	public List<Film> getCommonFilms(Long userId, Long friendId) {
		return jdbc.query(FIND_COMMON_QUERY, mapper, userId, friendId);
	}

	@Override
	public List<Film> getFilmsByDirector(Long directorId) {
		return jdbc.query(FIND_FILMS_BY_DIRECTOR, mapper, directorId);
	}

	@Override
	public List<Film> getFilmsByDirectorSortedByYear(Long directorId) {
		return jdbc.query(FIND_FILMS_BY_DIRECTOR_SORTED_BY_YEAR, mapper, directorId);
	}

	@Override
	public List<Film> getFilmsByDirectorSortedByLikes(Long directorId) {
		return jdbc.query(FIND_FILMS_BY_DIRECTOR_SORTED_BY_LIKES, mapper, directorId);
	}

	@Override
	public List<Film> searchByTitle(String query) {
		return jdbc.query(SEARCH_BY_TITLE, mapper, "%" + query.toLowerCase() + "%");
	}

	@Override
	public List<Film> searchByDirector(Long directorId) {
		return jdbc.query(SEARCH_BY_DIRECTOR_ID, mapper, directorId);
	}

	@Override
	public List<Film> searchByTitleAndDirector(String query, Long directorId) {
		return jdbc.query(SEARCH_BY_TITLE_AND_DIRECTOR, mapper, directorId, "%" + query.toLowerCase() + "%");
	}
}