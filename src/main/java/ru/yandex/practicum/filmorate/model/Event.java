package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class Event {
    @JsonProperty("eventId")
    private Long id;
    private Long userId;
    private Long entityId;
    private EventType eventType;
    private OperationType operation;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Instant timestamp;
}
