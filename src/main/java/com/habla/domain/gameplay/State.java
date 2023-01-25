package com.habla.domain.gameplay;

import com.habla.domain.language.FlashCard;
import com.habla.domain.language.Vocable;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class State {
    private final ArrayList<FlashCard> remainingWords;
    private final ArrayList<FlashCard> completed;
    private final GameStatus status;
    private Vocable currentVocable;

    public State(ArrayList<FlashCard> flashcards) {
        this.remainingWords = flashcards;
        this.completed = new ArrayList<>();
        this.status = GameStatus.CREATED;
    }
}
