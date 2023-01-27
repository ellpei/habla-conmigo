package com.habla.domain.gameplay;

import com.habla.domain.language.FlashCard;
import com.habla.domain.language.Vocable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class State {
    private List<FlashCard> remainingWords;
    private List<FlashCard> completed;
    private GameStatus status;
    private Vocable currentVocable;

    public State() {
        this.completed = new ArrayList<>();
        this.remainingWords = new ArrayList<>();
        this.status = GameStatus.CREATED;
    }

}
