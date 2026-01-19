package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Director {
	private Long id;

	@NotBlank(message = "Имя режиссёра должно быть указано")
	private String name;

	public Director(Long id, String name) {
		this.id = id;
		this.name = name;
	}
}
