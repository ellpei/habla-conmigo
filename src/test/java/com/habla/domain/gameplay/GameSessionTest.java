package com.habla.domain.gameplay;

import com.habla.TestHelper;
import com.habla.domain.language.Language;
import com.habla.domain.language.Vocable;
import com.habla.exception.InvalidGameStateException;
import com.habla.response.GameSessionDTO;
import com.habla.service.DictionaryLoaderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameSessionTest {

    private static final Player TEST_PLAYER = new Player("testusername", Language.SWEDISH, 0L);
    private static final Player TEST_JOINER = new Player("joinerusername", Language.SPANISH, 0L);
    private static final int NUM_DESIRED_WORDS = 10;

    @Mock
    private DictionaryLoaderService mockDictionaryLoaderService = mock(DictionaryLoaderService.class);

    @Test
    void createGameSession() {
        GameSession gameSession = new GameSession(TEST_PLAYER, NUM_DESIRED_WORDS);

        assertThat(gameSession.getNumDesiredWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(gameSession.getPlayer1()).isEqualTo(TEST_PLAYER);
        assertThat(gameSession.getPlayer2()).isNull();
        assertThat(gameSession.getGameState().getStatus()).isEqualTo(GameStatus.CREATED);
        assertThat(gameSession.getGameState().getRemainingWords()).isEmpty();
        assertThat(gameSession.getGameState().getCompleted()).isEmpty();
        assertThat(gameSession.getGameState().getCurrentVocable()).isNull();
    }

    @Test
    void joinGameSessionStateChangeToReady() {
        GameSession gameSession = new GameSession(TEST_PLAYER, NUM_DESIRED_WORDS);

        GameSession res = gameSession.tryJoinSession(TEST_JOINER);

        assertThat(res.getNumDesiredWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getPlayer1()).isEqualTo(TEST_PLAYER);
        assertThat(res.getPlayer2()).isEqualTo(TEST_JOINER);
        assertThat(res.getGameState().getStatus()).isEqualTo(GameStatus.READY);
        assertThat(gameSession.getGameState().getRemainingWords()).isEmpty();
        assertThat(gameSession.getGameState().getCompleted()).isEmpty();
        assertThat(gameSession.getGameState().getCurrentVocable()).isNull();
    }

    @Test
    void startGameNotInReadyStateException() {
        GameSession gameSession = new GameSession(TEST_PLAYER, NUM_DESIRED_WORDS);

        InvalidGameStateException exception = assertThrows(InvalidGameStateException.class,
                () -> gameSession.startGame(mockDictionaryLoaderService));

        assertThat(exception.getMessage()).isEqualTo("Game not in READY state, cannot start");
    }

    @Test
    void startGameStateHappyPath() {
        GameSession gameSession = new GameSession(TEST_PLAYER, NUM_DESIRED_WORDS);
        gameSession.tryJoinSession(TEST_JOINER);
        ArrayList<Vocable> vocables = TestHelper.generateRandomVocableList(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS);
        when(mockDictionaryLoaderService.loadWords(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS)).thenReturn(vocables);

        GameSession res = gameSession.startGame(mockDictionaryLoaderService);

        assertThat(res.getNumDesiredWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getPlayer1()).isEqualTo(TEST_PLAYER);
        assertThat(res.getPlayer2()).isEqualTo(TEST_JOINER);
        assertThat(res.getGameState().getStatus()).isEqualTo(GameStatus.PLAYING);
        assertThat(gameSession.getGameState().getRemainingWords().size()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(gameSession.getGameState().getCompleted()).isEmpty();
        assertThat(gameSession.getGameState().getCurrentVocable()).isIn(vocables);
    }

    @Test
    void endGameNotInPlayingStateException() {
        GameSession gameSession = new GameSession(TEST_PLAYER, NUM_DESIRED_WORDS);

        InvalidGameStateException exception = assertThrows(InvalidGameStateException.class, gameSession::endGame);

        assertThat(exception.getMessage()).isEqualTo("Game not in PLAYING state, cannot end game");
    }

    @Test
    void endGameHappyPath() {
        GameSession gameSession = new GameSession(TEST_PLAYER, NUM_DESIRED_WORDS);
        gameSession.tryJoinSession(TEST_JOINER);
        ArrayList<Vocable> vocables = TestHelper.generateRandomVocableList(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS);
        when(mockDictionaryLoaderService.loadWords(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS)).thenReturn(vocables);
        gameSession.startGame(mockDictionaryLoaderService);

        GameSession res = gameSession.endGame();

        assertThat(res.getNumDesiredWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getPlayer1()).isEqualTo(TEST_PLAYER);
        assertThat(res.getPlayer2()).isEqualTo(TEST_JOINER);
        assertThat(res.getGameState().getStatus()).isEqualTo(GameStatus.FINISHED);
        assertThat(gameSession.getGameState().getRemainingWords().size()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(gameSession.getGameState().getCompleted()).isEmpty();
        assertThat(gameSession.getGameState().getCurrentVocable()).isIn(vocables);
    }

    @Test
    void toDto() {
        GameSession gameSession = new GameSession(TEST_PLAYER, NUM_DESIRED_WORDS);
        gameSession.tryJoinSession(TEST_JOINER);
        ArrayList<Vocable> vocables = TestHelper.generateRandomVocableList(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS);
        when(mockDictionaryLoaderService.loadWords(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS)).thenReturn(vocables);
        gameSession.startGame(mockDictionaryLoaderService);

        GameSessionDTO res = gameSession.toDto();

        assertThat(res.getNumDesiredWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getPlayer1()).isEqualTo(TEST_PLAYER);
        assertThat(res.getPlayer2()).isEqualTo(TEST_JOINER);
        assertThat(res.getStatus()).isEqualTo("PLAYING");
        assertThat(res.getNumRemainingWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getCompleted().size()).isEqualTo(0);
    }

}