package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorRowMapper implements RowMapper<Director> {
	@Override
	public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
		Long id = rs.getLong("director_id");
		if (rs.wasNull()) {
			throw new SQLException("director_id не может быть null");
		}
		String name = rs.getString("name");

		return new Director(id, name);
	}
}
