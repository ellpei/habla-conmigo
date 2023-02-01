package com.habla.service;

import com.habla.TestHelper;
import com.habla.controller.UserDTO;
import com.habla.domain.language.Language;
import com.habla.domain.language.Vocable;
import com.habla.exception.InvalidGameStateException;
import com.habla.exception.SessionNotFoundException;
import com.habla.response.GameSessionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class SessionHandlerTest {

    private SessionHandler sessionHandler;

    @Mock
    DictionaryLoaderService mockDictionaryLoaderService = mock(DictionaryLoaderService.class);

    @BeforeEach
    void setUp() {
        sessionHandler = new SessionHandler(mockDictionaryLoaderService);
    }

    private static Stream<Arguments> happyPathParameters() {
        return Stream.of(
                Arguments.of("username1", "spanish", 0),
                Arguments.of("u", "SPANISH", 10),
                Arguments.of("testtest", "Swedish", 10),
                Arguments.of("testtest2", "SWEDISH", 100),
                Arguments.of("testtest3", "ENGLISH", -1),
                Arguments.of("testtest5", "german", 1)
        );
    }

    @ParameterizedTest
    @MethodSource("happyPathParameters")
    void createSessionHappyPath(String username, String nativeLanguage, int numDesiredWords) throws InstantiationException {
        UserDTO creator = new UserDTO(username, nativeLanguage, numDesiredWords);

        final String sessionId = sessionHandler.createSession(creator);

        assertThat(sessionId).isNotNull();
    }

    private static Stream<Arguments> badInputParameters() {
        return Stream.of(
                Arguments.of("", "spanish", "Username must not be null"),
                Arguments.of(null, "spanish", "Username must not be null"),
                Arguments.of("validUsername", "", "Native language must not be null"),
                Arguments.of("validUsername", null, "Native language must not be null"),
                Arguments.of("validUsername", "xxx", "Native language not recognizable, must be one of [SPANISH, SWEDISH, ENGLISH, GERMAN]"),
                Arguments.of("validUsername", "nonExistentLanguage", "Native language not recognizable, must be one of [SPANISH, SWEDISH, ENGLISH, GERMAN]")
        );
    }

    @ParameterizedTest
    @MethodSource("badInputParameters")
    void createSessionBadInput(String username, String nativeLanguage, String errorMessage) {
        UserDTO creator = new UserDTO(username, nativeLanguage, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> sessionHandler.createSession(creator));

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void createSessionAtCapacityException() throws InstantiationException {
        SessionHandler sessionHandlerLowCapacity = new SessionHandler(mockDictionaryLoaderService, 1);
        UserDTO creator = new UserDTO("username", "swedish", 10);
        sessionHandlerLowCapacity.createSession(creator);

        InstantiationException exception = assertThrows(InstantiationException.class, () -> sessionHandlerLowCapacity.createSession(creator));

        assertThat(exception.getMessage()).isEqualTo("Sorry, we are at capacity right now");
    }

    @Test
    void numActiveSessionsZero() {
        int res = sessionHandler.numActiveSessions();

        assertThat(res).isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 5, 10})
    void numActiveSessionsIsOne(int numSessions) throws InstantiationException {
        for (int i = 0; i < numSessions; i++) {
            UserDTO creator = new UserDTO("username" + i, "swedish", 10);
            sessionHandler.createSession(creator);
        }

        int res = sessionHandler.numActiveSessions();

        assertThat(res).isEqualTo(numSessions);
    }

    @Test
    void retrieveSessionHappyPath() throws InstantiationException {
        UserDTO creator = new UserDTO("username", "swedish", 10);
        String sessionId = sessionHandler.createSession(creator);

        GameSessionDTO res = sessionHandler.retrieveSession(sessionId);

        assertThat(res).isNotNull();
        assertThat(res.getPlayer1().getUsername()).isEqualTo("username");
        assertThat(res.getPlayer1().getNativeLanguage().toString()).isEqualTo("SWEDISH");
        assertThat(res.getNumDesiredWords()).isEqualTo(10);
    }

    @Test
    void retrieveSessionCorrectId() throws InstantiationException {
        UserDTO creator1 = new UserDTO("username1", "swedish", 10);
        String sessionId1 = sessionHandler.createSession(creator1);
        UserDTO creator2 = new UserDTO("username2", "spanish", 11);
        sessionHandler.createSession(creator2);

        GameSessionDTO res = sessionHandler.retrieveSession(sessionId1);

        assertThat(res).isNotNull();
        assertThat(res.getPlayer1().getUsername()).isEqualTo("username1");
        assertThat(res.getPlayer1().getNativeLanguage().toString()).isEqualTo("SWEDISH");
        assertThat(res.getNumDesiredWords()).isEqualTo(10);
    }

    @Test
    void retrieveSessionNoneExistsException() {
        SessionNotFoundException exception = assertThrows(SessionNotFoundException.class,
                () -> sessionHandler.retrieveSession("XYZ"));

        assertThat(exception.getMessage()).isEqualTo("Session with id XYZ could not be found");
    }

    @Test
    void retrieveSessionWrongIdException() throws InstantiationException {
        UserDTO creator = new UserDTO("username", "swedish", 10);
        sessionHandler.createSession(creator);

        SessionNotFoundException exception = assertThrows(SessionNotFoundException.class,
                () -> sessionHandler.retrieveSession("XYZ"));

        assertThat(exception.getMessage()).isEqualTo("Session with id XYZ could not be found");
    }

    @Test
    void tryJoinSessionHappyPath() throws InstantiationException {
        UserDTO creator = new UserDTO("username1", "swedish", 20);
        String sessionId = sessionHandler.createSession(creator);
        UserDTO joiner = new UserDTO("joinername", "english");

        GameSessionDTO res = sessionHandler.tryJoinSession(joiner, sessionId);

        assertThat(res.getPlayer1().getUsername()).isEqualTo("username1");
        assertThat(res.getPlayer1().getNativeLanguage().toString()).isEqualTo("SWEDISH");
        assertThat(res.getPlayer2().getUsername()).isEqualTo("joinername");
        assertThat(res.getPlayer2().getNativeLanguage().toString()).isEqualTo("ENGLISH");
    }

    @ParameterizedTest
    @MethodSource("badInputParameters")
    void joinSessionBadInput(String username, String nativeLanguage, String errorMessage) throws InstantiationException {
        UserDTO creator = new UserDTO("username1", "swedish", 20);
        String sessionId = sessionHandler.createSession(creator);
        UserDTO joiner = new UserDTO(username, nativeLanguage);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionHandler.tryJoinSession(joiner, sessionId));

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void tryJoinSessionNotExistsException() {
        UserDTO joiner = new UserDTO("joinername", "english");
        String sessionId = "XYZ";

        SessionNotFoundException exception = assertThrows(SessionNotFoundException.class,
                () -> sessionHandler.tryJoinSession(joiner, sessionId));

        assertThat(exception.getMessage()).isEqualTo("Session with id XYZ could not be found");
    }

    @Test
    void startGameHappyPath() throws InstantiationException {
        int numDesiredWords = 20;
        UserDTO creator = new UserDTO("username1", "swedish", numDesiredWords);
        String sessionId = sessionHandler.createSession(creator);
        UserDTO joiner = new UserDTO("joinername", "spanish");
        sessionHandler.tryJoinSession(joiner, sessionId);
        ArrayList<Vocable> words = TestHelper.generateRandomVocableList(Language.SWEDISH, Language.SPANISH, numDesiredWords);
        when(mockDictionaryLoaderService.loadWords(Language.SWEDISH, Language.SPANISH, numDesiredWords))
                .thenReturn(words);

        GameSessionDTO res = sessionHandler.startGame(sessionId);

        assertThat(res.getStatus()).isEqualTo("PLAYING");
        assertThat(res.getNumRemainingWords()).isEqualTo(numDesiredWords);
        assertThat(res.getCompleted().size()).isEqualTo(0);
        assertThat(res.getCurrentFlashCard().getVocable()).isIn(words);
    }

    @Test
    void tryStartSessionNotExistsException() {
        String sessionId = "XYZ";

        SessionNotFoundException exception = assertThrows(SessionNotFoundException.class,
                () -> sessionHandler.startGame(sessionId));

        assertThat(exception.getMessage()).isEqualTo("Session with id XYZ could not be found");
    }

    @Test
    void startGameMissingPlayer2Exception() throws InstantiationException {
        int numDesiredWords = 20;
        UserDTO creator = new UserDTO("username1", "swedish", numDesiredWords);
        String sessionId = sessionHandler.createSession(creator);

        InvalidGameStateException exception = assertThrows(InvalidGameStateException.class,
                () -> sessionHandler.startGame(sessionId));

        assertThat(exception.getMessage()).isEqualTo("Game not in READY state, cannot start");
    }

    private String createAndStartSession(String player1, String player2) throws InstantiationException {
        UserDTO creator = new UserDTO(player1, "swedish", 20);
        String sessionId = sessionHandler.createSession(creator);
        UserDTO joiner = new UserDTO(player2, "spanish");
        sessionHandler.tryJoinSession(joiner, sessionId);
        ArrayList<Vocable> words = TestHelper.generateRandomVocableList(Language.SWEDISH, Language.SPANISH, 20);
        when(mockDictionaryLoaderService.loadWords(Language.SWEDISH, Language.SPANISH, 20))
                .thenReturn(words);
        sessionHandler.startGame(sessionId);
        return sessionId;
    }
    @Test
    void approveWord() throws InstantiationException {
        String player1Username = "playa1";
        String player2Username = "playa2";
        String sessionId = createAndStartSession(player1Username, player2Username);

        GameSessionDTO res = sessionHandler.approveWord(sessionId, player2Username);

        assertThat(res.getCurrentFlashCard().bothPassed()).isFalse();
        assertThat(res.getCurrentFlashCard().getPlayer1Passed()).isTrue();
        assertNull(res.getCurrentFlashCard().getPlayer2Passed());
        assertThat(res.getPlayer1().getPoints()).isEqualTo(1);
        assertThat(res.getPlayer2().getPoints()).isEqualTo(0);
    }

    @Test
    void failWord() throws InstantiationException {
        String player1Username = "playa1";
        String player2Username = "playa2";
        String sessionId = createAndStartSession(player1Username, player2Username);

        GameSessionDTO res = sessionHandler.failWord(sessionId, player2Username);

        assertThat(res.getCurrentFlashCard().bothPassed()).isFalse();
        assertThat(res.getCurrentFlashCard().getPlayer1Passed()).isFalse();
        assertNull(res.getCurrentFlashCard().getPlayer2Passed());
        assertThat(res.getPlayer1().getPoints()).isEqualTo(0);
        assertThat(res.getPlayer2().getPoints()).isEqualTo(0);
    }

}