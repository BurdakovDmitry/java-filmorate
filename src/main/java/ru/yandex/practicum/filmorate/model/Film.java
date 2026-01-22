package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {
	private Long id;
	private String name;
	private String description;
	private LocalDate releaseDate;
	private int duration;
	private Set<Genre> genres;
	private Mpa mpa;
	private Set<Director> directors = new HashSet<>();

	public Film(String name, String description, LocalDate releaseDate, int duration, Set<Genre> genres) {
		this.name = name;
		this.description = description;
		this.releaseDate = releaseDate;
		this.duration = duration;
		this.genres = genres;
	}
}
