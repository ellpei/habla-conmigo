package com.habla.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class User {

    private String username; // TODO remember to add validation later
    private String nativeLanguage; // "SPANISH", "SWEDISH", "ENGLISH",...
    private int numDesiredWords;

    public User(String username, String nativeLanguage, int numDesiredWords) {
        this(username, nativeLanguage);
        this.numDesiredWords = numDesiredWords;
    }

    public User(String username, String nativeLanguage) {
        this.username = username;
        this.nativeLanguage = nativeLanguage;
    }
}

