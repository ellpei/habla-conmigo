package com.habla.domain.gameplay;

import com.habla.controller.UserDTO;
import com.habla.domain.language.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void create() {
        UserDTO dto = new UserDTO("usernamexx", "swedish");

        Player res = new Player(dto);

        assertThat(res.getUsername()).isEqualTo("usernamexx");
        assertThat(res.getNativeLanguage()).isEqualTo(Language.SWEDISH);
        assertThat(res.getPoints()).isEqualTo(0L);
    }

    private static Stream<Arguments> badInputParameters() {
        return Stream.of(
                Arguments.of("", "SWEDISH", "Username must not be null"),
                Arguments.of(null, "SWEDISH", "Username must not be null"),
                Arguments.of("username", "", "Native language must not be null"),
                Arguments.of("username", null, "Native language must not be null"),
                Arguments.of("username", "xxx", "Native language not recognizable, must be one of [SPANISH, SWEDISH, ENGLISH, GERMAN]")
        );
    }

    @ParameterizedTest
    @MethodSource("badInputParameters")
    void createBadInputError(String username, String nativeLanguage, String errorMessage) {
        UserDTO dto = new UserDTO(username, nativeLanguage);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Player(dto));

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }
}