package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
	Set<Director> getDirectors();

	Optional<Director> getDirectorById(Long id);

	Director createDirector(Director director);

	Optional<Director> updateDirector(Director director);

	boolean deleteDirector(Long id);

	void addDirectors(Long filmId, Set<Director> directors);

	void deleteDirectors(Long filmId);

	void updateFilmDirectors(Long filmId, Set<Director> directors);

	Set<Director> getDirectorsByFilm(Long filmId);

	void getDirectorsForFilms(List<Film> films);
}
