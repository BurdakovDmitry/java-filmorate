package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreDto mapToGenreDto(Genre genre);
}
