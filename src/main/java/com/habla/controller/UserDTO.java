package com.habla.controller;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class UserDTO {
    @NotEmpty(message = "The username is required.")
    private String username;

    @NotEmpty(message = "The native language is required")
    private String nativeLanguage; // "SPANISH", "SWEDISH", "ENGLISH",...
    private int numDesiredWords;

    public UserDTO(String username, String nativeLanguage, int numDesiredWords) {
        this(username, nativeLanguage);
        this.numDesiredWords = numDesiredWords;
    }

    public UserDTO(String username, String nativeLanguage) {
        this.username = username;
        this.nativeLanguage = nativeLanguage;
    }
}

