package com.habla.domain.gameplay;

import com.habla.exception.SessionAlreadyFullException;
import com.habla.response.GameSessionDTO;
import com.habla.service.DictionaryLoaderService;
import com.habla.domain.language.FlashCard;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class GameSession {
    private final Player player1;
    private Player player2;
    private State gameState;
    private final Integer numDesiredWords;

    @Autowired
    private DictionaryLoaderService dictionaryLoaderAdapter;

    public GameSession(Player creator, int numDesiredWords) {
        this.player1 = creator;
        this.numDesiredWords = numDesiredWords;
        this.gameState = new State();
    }

    public GameSession startGame() {
        assert (canStart()) : "not in ready state, cannot start game";
        List<FlashCard> flashCards = generateFlashCards();
        int randomNum = ThreadLocalRandom.current().nextInt(0, numDesiredWords + 1);
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
        return player1.isReady() && player2.isReady() && numDesiredWords > 0;
    }

    private List<FlashCard> generateFlashCards() {
        return dictionaryLoaderAdapter.loadWords(
                player1.getNativeLanguage(),
                player2.getNativeLanguage(),
                numDesiredWords)
                .stream().map(FlashCard::new)
                .collect(Collectors.toList());
    }

    public void endGame() {

    }

    public GameSessionDTO toDto() {
        return GameSessionDTO.builder()
                .player1(player1)
                .player2(player2)
                .completed(getGameState().getCompleted())
                .numDesiredWords(numDesiredWords)
                .numRemainingWords(getGameState().getRemainingWords().size())
                .status(gameState.getStatus().toString())
                .build();
    }
}
