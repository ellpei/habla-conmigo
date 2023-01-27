package com.habla.domain.dictionary;

import com.habla.domain.language.Language;
import com.habla.domain.language.Vocable;

import java.util.ArrayList;

public class CsvLoader extends DictionaryLoader {

    private String filePath;

    public CsvLoader() {
    }

    @Override
    public ArrayList<Vocable> loadWords(Language language1, Language language2, int numDesired) {
        return new ArrayList<>();
    }
}
