package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@Mapper(componentModel = "spring")
public interface DirectorMapper {
	DirectorDto mapToDirectorDto(Director director);

	Director mapToDirector(DirectorDto dto);
}

