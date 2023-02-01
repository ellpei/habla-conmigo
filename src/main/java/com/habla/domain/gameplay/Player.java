package com.habla.domain.gameplay;

import com.habla.controller.UserDTO;
import com.habla.domain.language.Language;
import com.habla.domain.util.InputHelper;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    private final String username;
    private final Language nativeLanguage;
    private Long points;

    public Player(UserDTO user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username must not be null");
        }
        if (user.getNativeLanguage() == null ||  user.getNativeLanguage().isBlank()) {
            throw new IllegalArgumentException("Native language must not be null");
        }
        this.username = user.getUsername();
        this.nativeLanguage = InputHelper.parseLanguage(user.getNativeLanguage());
        this.points = 0L;
    }

    public void addPoints(Long amount) {
        points += amount;
    }
}
