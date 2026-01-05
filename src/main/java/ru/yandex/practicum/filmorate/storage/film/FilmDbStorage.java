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
            "SELECT f.*, fm.mpa_name " +
            "FROM films AS f " +
            "LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id ";
    private static final String FIND_BY_ID_QUERY =
            "SELECT f.*, fm.mpa_name " +
            "FROM films AS f " +
            "LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id " +
            "WHERE f.film_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String FIND_POPULAR_QUERY =
            "SELECT f.*, fm.mpa_name " +
            "FROM films AS f " +
            "LEFT JOIN film_mpa AS fm ON f.mpa_id = fm.mpa_id " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "GROUP BY f.film_id, fm.mpa_name " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";

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
                film.getMpa().getId()
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
                film.getMpa().getId(),
                film.getId()
        );

        return film;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return jdbc.query(FIND_POPULAR_QUERY, mapper, count);
    }
}
