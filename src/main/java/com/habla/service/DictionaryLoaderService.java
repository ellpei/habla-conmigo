package com.habla.service;

import com.habla.domain.dictionary.CsvLoader;
import com.habla.domain.language.Language;
import com.habla.domain.language.Vocable;

import java.util.ArrayList;

public class DictionaryLoaderService {
    private CsvLoader csvLoader;

    public DictionaryLoaderService(CsvLoader csvLoader) {
        this.csvLoader = csvLoader;
    }

    public ArrayList<Vocable> loadWords(Language language1, Language language2, int numDesiredWords) {
        // pick correct loader
        return csvLoader.loadWords(language1, language2, numDesiredWords);
    }
}
