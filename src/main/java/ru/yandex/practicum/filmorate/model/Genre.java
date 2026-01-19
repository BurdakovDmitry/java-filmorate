package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
public class Genre {
	private Integer id;
	private String name;

	@Autowired
	public Genre(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
}
