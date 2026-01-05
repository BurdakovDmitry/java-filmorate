package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Genre> mapper;
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String FIND_BY_FILM_ID_QUERY =
            "SELECT g.genre_id, g.name " +
                    "FROM genre AS g " +
                    "JOIN film_genre AS fg ON g.genre_id = fg.genre_id " +
                    "WHERE fg.film_id = ? " +
                    "ORDER BY g.genre_id";

    @Override
    public Set<Genre> getGenre() {
        return new LinkedHashSet<>(jdbc.query(FIND_ALL_QUERY, mapper));
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        return jdbc.query(FIND_BY_ID_QUERY, mapper, id).stream().findFirst();
    }

    @Override
    public void addGenres(Long filmId, Set<Genre> genres) {
        for (Genre genre : genres) {
            jdbc.update(INSERT_QUERY, filmId, genre.getId());
        }
    }

    @Override
    public void deleteGenres(Long filmId) {
        jdbc.update(DELETE_QUERY, filmId);
    }

    @Override
    public void updateGenres(Long filmId, Set<Genre> genres) {
        deleteGenres(filmId);
        addGenres(filmId, genres);
    }

    @Override
    public Set<Genre> getGenresByFilm(Long filmId) {
        return new LinkedHashSet<>(jdbc.query(FIND_BY_FILM_ID_QUERY, mapper, filmId));
    }
}
