package com.habla.domain.gameplay;

import com.habla.domain.language.Language;
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

    public boolean isReady() {
        return isUsernameSet() && nativeLanguage != null;
    }

    private boolean isUsernameSet() {
        return username != null && username.length() != 0;
    }
}
