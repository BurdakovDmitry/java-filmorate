package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());

        if (film.getMpa() != null) {
            MpaDto mpaDto = new MpaDto();
            mpaDto.setId(film.getMpa().getId());
            mpaDto.setName(film.getMpa().getName());
            filmDto.setMpa(mpaDto);
        }

        if (film.getGenres() != null) {
            filmDto.setGenres(film.getGenres().stream()
                    .map(GenreMapper::mapToGenreDto)
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        } else {
            filmDto.setGenres(new LinkedHashSet<>());
        }

        return filmDto;
    }
}
