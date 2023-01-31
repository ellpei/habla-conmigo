package com.habla.domain.gameplay;

import com.habla.exception.InvalidGameStateException;
import com.habla.exception.SessionAlreadyFullException;
import com.habla.response.GameSessionDTO;
import com.habla.service.DictionaryLoaderService;
import com.habla.domain.language.FlashCard;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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
        int randomNum = ThreadLocalRandom.current().nextInt(0, numDesiredWords);
        gameState.setRemainingWords(flashCards);
        gameState.setCurrentVocable(flashCards.get(randomNum).getVocable());
        gameState.setStatus(GameStatus.PLAYING);
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

    public GameSession endGame() {
        if (gameState.getStatus() != GameStatus.PLAYING) {
            throw new InvalidGameStateException("Game not in PLAYING state, cannot end game");
        }
        gameState.setStatus(GameStatus.FINISHED);
        return this;
    }

    public GameSessionDTO toDto() {
        return GameSessionDTO.builder()
                .player1(player1)
                .player2(player2)
                .completed(getGameState().getCompleted())
                .numDesiredWords(numDesiredWords)
                .numRemainingWords(getGameState().getRemainingWords().size())
                .status(gameState.getStatus().toString())
                .currentVocable(gameState.getCurrentVocable())
                .build();
    }
}
