package com.habla.domain.util;

import com.habla.domain.language.Language;

import java.util.Arrays;

public class InputHelper {

    public static Language parseLanguage(String nativeLanguage) {
        try {
            return Language.valueOf(nativeLanguage.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format(
                            "Native language not recognizable, must be one of %s",
                            Arrays.toString(Language.values())));
        }
    }
}
