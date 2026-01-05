package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<MpaDto> getMpa() {
        List<MpaDto> mpa = mpaStorage.getMpa()
                .stream()
                .map(MpaMapper::mapToMpaDto)
                .toList();

        log.info("Получен список рейтингов MPA: {}", mpa);
        return mpa;
    }

    public MpaDto getMpaById(Integer id) {
        return mpaStorage.getMpaById(id)
                .map(MpaMapper::mapToMpaDto)
                .orElseThrow(() -> {
                    log.warn("Рейтинг MPA с id = {} не найден", id);
                    return new NotFoundException("MPA с id = " + id + " не найден");
                });
    }
}
