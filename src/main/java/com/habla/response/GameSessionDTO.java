package com.habla.response;

import com.habla.domain.gameplay.Player;
import com.habla.domain.language.FlashCard;
import com.habla.domain.language.Vocable;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GameSessionDTO {
    private String status;

    private Player player1;
    private Player player2;

    private final Integer numDesiredWords;
    private int numRemainingWords;
    private final List<FlashCard> completed;
    private Vocable currentVocable;

}
