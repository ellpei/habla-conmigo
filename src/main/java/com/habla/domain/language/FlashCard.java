package com.habla.domain.language;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public boolean isComplete() {
        return this.player1Completed && this.player2Completed;
    }
}
