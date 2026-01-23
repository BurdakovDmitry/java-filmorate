package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Slf4j
@RequiredArgsConstructor
public class BaseRepository {
	protected final JdbcTemplate jdbc;

	protected long insert(String query, Object... params) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		jdbc.update(connection -> {
			PreparedStatement ps = connection
				.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			for (int idx = 0; idx < params.length; idx++) {
				ps.setObject(idx + 1, params[idx]);
			}
			return ps;
		}, keyHolder);

		Long id = keyHolder.getKeyAs(Long.class);
		if (id != null) {
			return id;
		} else {
			log.warn("Ошибка при сохранении данных в БД: ключ не получен.");
			throw new InternalServerException("Не удалось сохранить данные");
		}
	}

	protected void update(String query, Object... params) {
		int rowsUpdated = jdbc.update(query, params);
		if (rowsUpdated == 0) {
			log.warn("Ошибка при обновлении данных. Запись не найдена");
			throw new NotFoundException("Запись для обновления не найдена");
		}
	}
}
