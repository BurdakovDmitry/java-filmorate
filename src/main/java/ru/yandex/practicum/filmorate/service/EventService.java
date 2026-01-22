package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;

import java.util.List;

@Service
public class EventService {
    private final EventDbStorage storage;

    public EventService(EventDbStorage storage) {
        this.storage = storage;
    }

    public void send(Event event) {
        storage.createEvent(event);
    }

    public List<Event> getByUserId(Long userId) {
        if (userId == null) {
            return storage.getEvents();
        }
        return storage.getEvents(userId);
    }


}
