package com.habla.domain.dictionary;

import com.habla.domain.language.Language;
import com.habla.domain.language.Vocable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CsvLoaderTest {

    private final CsvLoader csvLoader = new CsvLoader();

    @Test
    void loadWords() {
        Language language1 = Language.SWEDISH;
        Language language2 = Language.SPANISH;

        ArrayList<Vocable> res = csvLoader.loadWords(language1, language2, 10);

        assertThat(res.size()).isEqualTo(10);
        assertThat(res.get(0).word1().language()).isEqualTo(Language.SPANISH);
        assertThat(res.get(0).word2().language()).isEqualTo(Language.SWEDISH);
    }

    @Test
    void loadWordsLanguageMismatch() {
        Language language1 = Language.ENGLISH;
        Language language2 = Language.SPANISH;

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> csvLoader.loadWords(language1, language2, 10));

        assertThat(exception.getMessage()).isEqualTo("CSV language in header SWEDISH not matching one of ENGLISH or SPANISH");
    }
}