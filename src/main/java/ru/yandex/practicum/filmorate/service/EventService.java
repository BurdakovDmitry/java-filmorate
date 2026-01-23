package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.validation.Validation;

import java.util.List;

@Service
public class EventService {
    private final EventDbStorage storage;
    private final Validation validation;

    public EventService(EventDbStorage storage, Validation validation) {
        this.storage = storage;
        this.validation = validation;
    }

    public void send(Event event) {
        storage.createEvent(event);
    }

    public List<Event> getByUserId(Long userId) {
        validation.userById(userId);

        return storage.getEvents(userId);
    }


}
