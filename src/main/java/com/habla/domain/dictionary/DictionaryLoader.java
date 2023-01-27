package com.habla.domain.dictionary;

import com.habla.domain.language.Language;
import com.habla.domain.language.Vocable;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public abstract class DictionaryLoader {
    abstract List<Vocable> loadWords(Language language1, Language language2, int numDesired);
}
