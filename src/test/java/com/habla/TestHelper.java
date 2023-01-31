package com.habla;

import com.habla.domain.language.Language;
import com.habla.domain.language.Vocable;
import com.habla.domain.language.Word;

import java.util.ArrayList;

public class TestHelper {

    public static ArrayList<Vocable> generateRandomVocableList(Language language1, Language language2, int size) {
        ArrayList<Vocable> vocables = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Word word1 = new Word(language1, "xxx" + i);
            Word word2 = new Word(language2, "yyy" + i);
            vocables.add(new Vocable(word1, word2));
        }
        return vocables;
    }
}
