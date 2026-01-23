package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Event {
    @JsonProperty("eventId")
    private Long id;
    private Long userId;
    private Long entityId;
    private EventType eventType;
    private OperationType operation;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Instant timestamp;

    public Event(Long userId, Long entityId, EventType eventType, OperationType operation, Instant timestamp) {
        this.userId = userId;
        this.entityId = entityId;
        this.eventType = eventType;
        this.operation = operation;
        this.timestamp = timestamp;
    }
}
