package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Director {
	private Integer id;
	private String name;

	public Director(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
}
