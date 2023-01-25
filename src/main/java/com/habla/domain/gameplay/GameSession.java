package com.habla.domain.gameplay;

import com.habla.domain.language.FlashCard;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class GameSession {
    private final Player player1;
    private Player player2;
    private State gameState;
    private final Integer numDesiredWords;

    public GameSession(Player creator, int numDesiredWords) {
        this.player1 = creator;
        this.numDesiredWords = numDesiredWords;
    }

    public void setPlayer2(Player player) {
        this.player2 = player;
    }

    public boolean canStart() {
        return player1.isReady() && player2.isReady();
    }

    public void startGame() {
        ArrayList<FlashCard> flashCards = generateFlashCards(numDesiredWords);
        this.gameState = new State(flashCards);
    }

    private ArrayList<FlashCard> generateFlashCards(int numDesiredWords) {
        return new ArrayList<>();
    }

    public void endGame() {

    }
}
