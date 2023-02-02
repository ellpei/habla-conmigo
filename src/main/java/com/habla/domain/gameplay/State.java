package com.habla.domain.gameplay;

import com.habla.domain.language.FlashCard;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class State {
    private List<FlashCard> remainingWords;
    private List<FlashCard> completed;
    private GameStatus status;
    private FlashCard currentFlashCard;

    public State() {
        this.completed = new ArrayList<>();
        this.remainingWords = new ArrayList<>();
        this.status = GameStatus.CREATED;
    }

    public void setPlayer1CompletedWord(boolean passed) {
        currentFlashCard.setPlayer1Passed(passed);
        maybeCompleteCurrentWord();
    }

    public void setPlayer2CompletedWord(boolean passed) {
        currentFlashCard.setPlayer2Passed(passed);
        maybeCompleteCurrentWord();
    }

    public void maybeCompleteCurrentWord() {
        if (currentFlashCard.isComplete()) {
            getCompleted().add(getCurrentFlashCard());
            getRemainingWords().remove(getCurrentFlashCard());
            randomizeCurrentFlashCard();
        }

    }

    public void randomizeCurrentFlashCard() {
        if (remainingWords.size() == 0) {
            status = GameStatus.FINISHED;
            setCurrentFlashCard(null);
            return;
        }
        int randomNum = ThreadLocalRandom.current().nextInt(0, remainingWords.size());
        setCurrentFlashCard(remainingWords.get(randomNum));
    }

}
