package com.habla.domain.gameplay;

import com.habla.exception.InvalidGameStateException;
import com.habla.exception.SessionAlreadyFullException;
import com.habla.response.GameSessionDTO;
import com.habla.service.DictionaryLoaderService;
import com.habla.domain.language.FlashCard;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class GameSession {
    private final Player player1;
    private Player player2;
    private final State gameState;
    private final Integer numDesiredWords;

    public GameSession(Player creator, int numDesiredWords) {
        this.player1 = creator;
        this.numDesiredWords = numDesiredWords;
        this.gameState = new State();
    }

    public GameSession startGame(DictionaryLoaderService dictionaryLoaderService) {
        if (!canStart()) {
            throw new InvalidGameStateException("Game not in READY state, cannot start");
        }
        List<FlashCard> flashCards = generateFlashCards(dictionaryLoaderService);
        gameState.setRemainingWords(flashCards);
        gameState.setStatus(GameStatus.PLAYING);
        gameState.randomizeCurrentFlashCard();
        return this;
    }

    public GameSession tryJoinSession(Player player2) {
        if (this.player2 != null) {
            throw new SessionAlreadyFullException("Session already full, cannot join");
        }
        this.player2 = player2;
        gameState.setStatus(GameStatus.READY);
        return this;
    }

    private boolean canStart() {
        return player1 != null && player2 != null && numDesiredWords > 0;
    }

    private List<FlashCard> generateFlashCards(DictionaryLoaderService dictionaryLoaderService) {
        return dictionaryLoaderService.loadWords(
                player1.getNativeLanguage(),
                player2.getNativeLanguage(),
                numDesiredWords)
                .stream().map(FlashCard::new)
                .collect(Collectors.toList());
    }

    public GameSession approveWord(String approverUsername) {
        assertCorrectState(GameStatus.PLAYING);
        return updateCurrentFlashCard(approverUsername, true);
    }

    public GameSession failWord(String approverUsername) {
        assertCorrectState(GameStatus.PLAYING);
        return updateCurrentFlashCard(approverUsername, false);
    }

    private GameSession updateCurrentFlashCard(String approverUsername, boolean opponentPassed) {
        if (Objects.equals(getPlayer1().getUsername(), approverUsername)) {
            gameState.setPlayer2CompletedWord(opponentPassed);
            return opponentPassed ? addCompletionPoints(player2, player1) : this;
        } else if (Objects.equals(getPlayer2().getUsername(), approverUsername)) {
            gameState.setPlayer1CompletedWord(opponentPassed);
            return opponentPassed ? addCompletionPoints(player1, player2) : this;
        }
        return this;
    }

    private GameSession addCompletionPoints(Player approvedPlayer, Player approver) {
        // Both player gets points to incentivise helping each other out
        approvedPlayer.addPoints(10L);
        approver.addPoints(5L);
        return this;
    }

    public GameSession endGame() {
        assertCorrectState(GameStatus.PLAYING);
        gameState.setStatus(GameStatus.FINISHED);
        return this;
    }

    private void assertCorrectState(GameStatus status) {
        if (gameState.getStatus() != status) {
            throw new InvalidGameStateException(
                    String.format("Game not in %s state, cannot perform operation", status.toString()));
        }
    }

    public GameSessionDTO toDto() {
        return GameSessionDTO.builder()
                .player1(player1)
                .player2(player2)
                .completed(getGameState().getCompleted())
                .numDesiredWords(numDesiredWords)
                .numRemainingWords(getGameState().getRemainingWords().size())
                .status(gameState.getStatus().toString())
                .currentFlashCard(gameState.getCurrentFlashCard())
                .build();
    }
}
