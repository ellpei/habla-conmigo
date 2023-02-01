package com.habla.domain.gameplay;

import com.habla.TestHelper;
import com.habla.domain.language.FlashCard;
import com.habla.domain.language.Language;
import com.habla.domain.language.Vocable;
import com.habla.exception.InvalidGameStateException;
import com.habla.response.GameSessionDTO;
import com.habla.service.DictionaryLoaderService;
import com.habla.service.SessionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameSessionTest {

    private static Player TEST_PLAYER;
    private static Player TEST_JOINER;
    private static final int NUM_DESIRED_WORDS = 10;

    private GameSession gameSession;

    @Mock
    private DictionaryLoaderService mockDictionaryLoaderService = mock(DictionaryLoaderService.class);

    @BeforeEach
    void setUp() {
        gameSession = new GameSession(TEST_PLAYER, NUM_DESIRED_WORDS);
        TEST_PLAYER = new Player("testusername", Language.SWEDISH, 0L);
        TEST_JOINER = new Player("testjoiner", Language.SPANISH, 0L);
    }

    @Test
    void createGameSession() {
        GameSession gameSession = new GameSession(TEST_PLAYER, NUM_DESIRED_WORDS);

        assertThat(gameSession.getNumDesiredWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(gameSession.getPlayer1()).isEqualTo(TEST_PLAYER);
        assertThat(gameSession.getPlayer2()).isNull();
        assertThat(gameSession.getGameState().getStatus()).isEqualTo(GameStatus.CREATED);
        assertThat(gameSession.getGameState().getRemainingWords()).isEmpty();
        assertThat(gameSession.getGameState().getCompleted()).isEmpty();
        assertThat(gameSession.getGameState().getCurrentFlashCard()).isNull();
    }

    @Test
    void joinGameSessionStateChangeToReady() {
        GameSession res = gameSession.tryJoinSession(TEST_JOINER);

        assertThat(res.getNumDesiredWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getPlayer1()).isEqualTo(TEST_PLAYER);
        assertThat(res.getPlayer2()).isEqualTo(TEST_JOINER);
        assertThat(res.getGameState().getStatus()).isEqualTo(GameStatus.READY);
        assertThat(res.getGameState().getRemainingWords()).isEmpty();
        assertThat(res.getGameState().getCompleted()).isEmpty();
        assertThat(res.getGameState().getCurrentFlashCard()).isNull();
    }

    @Test
    void startGameNotInReadyStateException() {
        InvalidGameStateException exception = assertThrows(InvalidGameStateException.class,
                () -> gameSession.startGame(mockDictionaryLoaderService));

        assertThat(exception.getMessage()).isEqualTo("Game not in READY state, cannot start");
    }

    @Test
    void startGameStateHappyPath() {
        gameSession.tryJoinSession(TEST_JOINER);
        ArrayList<Vocable> vocables = TestHelper.generateRandomVocableList(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS);
        when(mockDictionaryLoaderService.loadWords(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS)).thenReturn(vocables);

        GameSession res = gameSession.startGame(mockDictionaryLoaderService);

        assertThat(res.getNumDesiredWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getPlayer1()).isEqualTo(TEST_PLAYER);
        assertThat(res.getPlayer2()).isEqualTo(TEST_JOINER);
        assertThat(res.getGameState().getStatus()).isEqualTo(GameStatus.PLAYING);
        assertThat(res.getGameState().getRemainingWords().size()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getGameState().getCompleted()).isEmpty();
        assertThat(res.getGameState().getCurrentFlashCard().getVocable()).isIn(vocables);
        assertThat(res.getGameState().getCurrentFlashCard().getPlayer1Passed()).isNull();
        assertThat(res.getGameState().getCurrentFlashCard().getPlayer2Passed()).isNull();
    }

    @Test
    void endGameNotInPlayingStateException() {
        InvalidGameStateException exception = assertThrows(InvalidGameStateException.class, gameSession::endGame);

        assertThat(exception.getMessage()).isEqualTo("Game not in PLAYING state, cannot perform operation");
    }

    @Test
    void endGameHappyPath() {
        gameSession.tryJoinSession(TEST_JOINER);
        ArrayList<Vocable> vocables = TestHelper.generateRandomVocableList(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS);
        when(mockDictionaryLoaderService.loadWords(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS)).thenReturn(vocables);
        gameSession.startGame(mockDictionaryLoaderService);

        GameSession res = gameSession.endGame();

        assertThat(res.getNumDesiredWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getPlayer1()).isEqualTo(TEST_PLAYER);
        assertThat(res.getPlayer2()).isEqualTo(TEST_JOINER);
        assertThat(res.getGameState().getStatus()).isEqualTo(GameStatus.FINISHED);
        assertThat(res.getGameState().getRemainingWords().size()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getGameState().getCompleted()).isEmpty();
        assertThat(res.getGameState().getCurrentFlashCard().getVocable()).isIn(vocables);
    }

    @Test
    void completeWordBothPlayersPassed() {
        gameSession.tryJoinSession(TEST_JOINER);
        ArrayList<Vocable> vocables = TestHelper.generateRandomVocableList(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS);
        when(mockDictionaryLoaderService.loadWords(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS)).thenReturn(vocables);
        gameSession = gameSession.startGame(mockDictionaryLoaderService);
        FlashCard originalFlashCard = gameSession.getGameState().getCurrentFlashCard();

        gameSession.approveWord(TEST_JOINER.getUsername());
        gameSession = gameSession.approveWord(TEST_PLAYER.getUsername());

        assertThat(gameSession.getGameState().getStatus()).isEqualTo(GameStatus.PLAYING);
        assertThat(gameSession.getGameState().getRemainingWords().size()).isEqualTo(NUM_DESIRED_WORDS-1);
        assertThat(gameSession.getGameState().getCompleted().size()).isEqualTo(1);
        assertThat(gameSession.getGameState().getCompleted().get(0)).isEqualTo(originalFlashCard);
        assertThat(gameSession.getGameState().getCurrentFlashCard()).isNotEqualTo(originalFlashCard);
        assertThat(gameSession.getPlayer1().getPoints()).isEqualTo(1L);
        assertThat(gameSession.getPlayer2().getPoints()).isEqualTo(1L);
    }

    @Test
    void approveWordOnePlayerPassed() {
        gameSession.tryJoinSession(TEST_JOINER);
        ArrayList<Vocable> vocables = TestHelper.generateRandomVocableList(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS);
        when(mockDictionaryLoaderService.loadWords(Language.SWEDISH, Language.SPANISH, NUM_DESIRED_WORDS)).thenReturn(vocables);
        gameSession = gameSession.startGame(mockDictionaryLoaderService);
        FlashCard originalFlashCard = gameSession.getGameState().getCurrentFlashCard();

        gameSession = gameSession.approveWord(TEST_PLAYER.getUsername());

        assertThat(gameSession.getGameState().getStatus()).isEqualTo(GameStatus.PLAYING);
        assertThat(gameSession.getGameState().getRemainingWords().size()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(gameSession.getGameState().getCompleted().size()).isEqualTo(0);
        assertThat(gameSession.getGameState().getCurrentFlashCard()).isEqualTo(originalFlashCard);
        assertThat(gameSession.getPlayer1().getPoints()).isEqualTo(0L);
        assertThat(gameSession.getPlayer2().getPoints()).isEqualTo(1L);
    }

    @Test
    void toDto() {
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