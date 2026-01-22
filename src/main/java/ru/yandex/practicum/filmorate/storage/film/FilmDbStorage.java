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
    private final RowMapper<Film> mapper;
    private final GenreStorage genreStorage;
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
            "ORDER BY COUNT(DISTINCT l.user_id) DESC " +
            "LIMIT ?";

    private static final String FIND_COMMON_QUERY =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name " +
            "FROM films AS f " +
            "JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id " +
            "WHERE f.film_id IN (SELECT l1.film_id " +
                    "FROM likes AS l1 " +
                    "JOIN likes AS l2 ON l1.film_id = l2.film_id " +
                    "WHERE l1.user_id = ? AND l2.user_id = ?) " +
            "ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) DESC";

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
    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        List<Object> params = new ArrayList<>();
        if (genreId == null && year == null) {
            return jdbc.query(FIND_POPULAR_QUERY, mapper, count);
        }

        StringBuilder queryBuilder = new StringBuilder(
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name " +
            "FROM films AS f " +
            "LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id "
        );
        if (genreId != null) {
            queryBuilder.append(" INNER JOIN film_genre AS fg ON f.film_id = fg.film_id ");
        }

        queryBuilder.append(" WHERE 1=1 ");
        if (genreId != null) {
            queryBuilder.append(" AND fg.genre_id = ? ");
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
        genreStorage.getGenresForFilms(films);
        return films;
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return jdbc.query(FIND_COMMON_QUERY, mapper, userId, friendId);
    }
}
