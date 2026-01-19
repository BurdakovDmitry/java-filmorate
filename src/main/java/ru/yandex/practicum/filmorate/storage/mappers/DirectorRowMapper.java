package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorRowMapper implements RowMapper<Director> {
	@Override
	public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
		Integer id = rs.getInt("director_id");
		String name = rs.getString("name");

		if (rs.wasNull()) {
			throw new SQLException("director_id cannot be null");
		}

		return new Director(id, name);
	}
}
