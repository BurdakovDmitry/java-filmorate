package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseRepository implements FilmStorage {
    private final RowMapper<Film> mapper;
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

    private static final String FIND_COMMON_QUERY =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, fm.mpa_name " +
            "FROM films AS f " +
            "JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id " +
            "WHERE f.film_id IN (SELECT l1.film_id " +
                    "FROM likes AS l1 " +
                    "JOIN likes AS l2 ON l1.film_id = l2.film_id " +
                    "WHERE l1.user_id = ? AND l2.user_id = ?) " +
            "ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) DESC";

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
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return jdbc.query(FIND_COMMON_QUERY, mapper, userId, friendId);
    }
}
