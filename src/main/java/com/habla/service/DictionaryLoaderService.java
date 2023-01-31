package com.habla.service;

import com.habla.domain.dictionary.CsvLoader;
import com.habla.domain.language.Language;
import com.habla.domain.language.Vocable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class DictionaryLoaderService {
    @Autowired
    private CsvLoader csvLoader;

    public DictionaryLoaderService() {
    }

    public ArrayList<Vocable> loadWords(Language language1, Language language2, int numDesiredWords) {
        // pick correct loader
        return csvLoader.loadWords(language1, language2, numDesiredWords);
    }
}
