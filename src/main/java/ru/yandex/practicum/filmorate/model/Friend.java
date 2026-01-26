package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Friend {
	private Long friendId;
	private Long userId;
	private boolean status;
}
