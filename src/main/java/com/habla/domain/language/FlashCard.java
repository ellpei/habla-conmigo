package com.habla.domain.language;

public class FlashCard {
    private Vocable vocable;
    private boolean player1Completed;
    private boolean player2Completed;
    private int timesDisplayed;

    public FlashCard(Vocable vocable) {
        this.vocable = vocable;
        this.player1Completed = false;
        this.player2Completed = false;
        this.timesDisplayed = 0;
    }

    public Vocable displayVocable() {
        this.timesDisplayed++;
        return vocable;
    }

    public int getTimesDisplayed() {
        return this.timesDisplayed;
    }

    public void setPlayer1Completed() {
        this.player1Completed = true;
    }

    public void setPlayer2Completed() {
        this.player2Completed = true;
    }

    public boolean isComplete() {
        return this.player1Completed && this.player2Completed;
    }
}
