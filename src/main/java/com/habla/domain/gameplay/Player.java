package com.habla.domain.gameplay;

import com.habla.controller.UserDTO;
import com.habla.domain.language.Language;
import com.habla.domain.util.InputHelper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private String username;
    private Language nativeLanguage;
    private Long points;

    public Player(String username, Language nativeLanguage) {
        this(username);
        this.nativeLanguage = nativeLanguage;
    }

    public Player(String username) {
        this.username = username;
        this.points = 0L;
    }

    public static Player create(UserDTO user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username must not be null");
        }
        if (user.getNativeLanguage() == null ||  user.getNativeLanguage().isBlank()) {
            throw new IllegalArgumentException("Native language must not be null");
        }
        return new Player(user.getUsername(), InputHelper.parseLanguage(user.getNativeLanguage()));
    }

    public boolean isReady() {
        return isUsernameSet() && nativeLanguage != null;
    }

    private boolean isUsernameSet() {
        return username != null && username.length() != 0;
    }
}
