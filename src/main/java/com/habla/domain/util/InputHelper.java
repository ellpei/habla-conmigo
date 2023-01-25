package com.habla.domain.util;

import com.habla.domain.language.Language;

public class InputHelper {

    public static Language parseLanguage(String nativeLanguage) {
        try {
            return Language.valueOf(nativeLanguage.toUpperCase());
        } catch (Exception e) {
            return Language.ENGLISH;
        }
    }
}
