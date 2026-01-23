package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;

@Repository
public class EventDbStorage extends BaseRepository implements EventStorage {
    private final RowMapper<Event> mapper;
    private static final String FIND_BY_ID_QUERY =
            "SELECT event_type, operation_type, entity_id, time_stamp, user_id, id " +
            "FROM events AS f " +
            "WHERE f.user_id IN " +
                    "(SELECT friend_id FROM friends " +
                    "WHERE user_id = ?" +
                    "UNION " +
                    "SELECT user_id FROM users WHERE user_id = ?)";
    private static final String FIND_ALL_QUERY =
            "SELECT event_type, operation_type, entity_id, time_stamp, user_id, id " +
                    "FROM events ";
    private static final String INSERT_QUERY =
            "INSERT INTO events (event_type, operation_type, entity_id, time_stamp, user_id) " +
                    "VALUES (?, ?, ?, ?, ?)";



    public EventDbStorage(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc);
        this.mapper = mapper;
    }


    @Override
    public Event createEvent(Event event) {
        event.setId(super.insert(INSERT_QUERY,
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId(),
                event.getTimestamp().toEpochMilli(),
                event.getUserId()));
        return event;
    }

    @Override
    public List<Event> getEvents(Long userId) {
        return jdbc.query(FIND_BY_ID_QUERY, mapper, userId, userId);
    }

    @Override
    public List<Event> getEvents() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }
}
