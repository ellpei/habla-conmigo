package com.habla.domain.language;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlashCard {
    private Vocable vocable;
    private Boolean player1Passed;
    private Boolean player2Passed;
    private int timesDisplayed;

    public FlashCard(Vocable vocable) {
        this.vocable = vocable;
        this.timesDisplayed = 0;
    }

    public Vocable displayVocable() {
        this.timesDisplayed++;
        return vocable;
    }

    public boolean isComplete() {
        return this.player1Passed != null && this.player2Passed != null;
    }

    public boolean bothPassed() {
        return player1Passed != null && player1Passed
                && player2Passed != null && player2Passed;
    }
}
