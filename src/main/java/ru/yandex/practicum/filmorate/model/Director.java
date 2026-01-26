package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Director {
	private Long id;

	@NotBlank(message = "Имя режиссёра должно быть указано")
	@Size(max = 200)
	private String name;

	public Director(Long id, String name) {
		this.id = id;
		this.name = name;
	}
}
