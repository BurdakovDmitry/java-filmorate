package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({MpaDbStorage.class, MpaRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {
    private final MpaDbStorage mpaStorage;

    @Test
    public void getMpa() {
        List<Mpa> mpaList = mpaStorage.getMpa();

        assertThat(mpaList)
                .isNotEmpty()
                .hasSize(5)
                .extracting(Mpa::getName)
                .containsExactly("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    public void getMpaById() {
        Optional<Mpa> mpaOptional = mpaStorage.getMpaById(1);

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
                });
    }

    @Test
    public void getMpaUnknownId() {
        Optional<Mpa> mpaOptional = mpaStorage.getMpaById(100);

        assertThat(mpaOptional).isEmpty();
    }
}
