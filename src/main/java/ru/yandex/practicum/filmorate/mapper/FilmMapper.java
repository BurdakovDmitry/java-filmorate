package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@Mapper(componentModel = "spring")
public interface FilmMapper {
    FilmDto mapToFilmDto(Film film);
}
