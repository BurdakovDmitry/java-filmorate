package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
	private static final String FIND_ALL_QUERY =
		"SELECT mpa_id, mpa_name FROM film_mpa ORDER BY mpa_id";
	private static final String FIND_BY_ID_QUERY =
		"SELECT mpa_id, mpa_name FROM film_mpa WHERE mpa_id = ?";

	private final JdbcTemplate jdbc;
	private final RowMapper<Mpa> mapper;

	@Override
	public List<Mpa> getMpa() {
		return jdbc.query(FIND_ALL_QUERY, mapper);
	}

	@Override
	public Optional<Mpa> getMpaById(Integer id) {
		return jdbc.query(FIND_BY_ID_QUERY, mapper, id).stream().findFirst();
	}
}
