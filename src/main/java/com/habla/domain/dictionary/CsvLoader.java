package com.habla.domain.dictionary;

import com.habla.domain.language.Language;
import com.habla.domain.language.Vocable;
import com.habla.domain.language.Word;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CsvLoader extends DictionaryLoader {

    private String filePath = "src/main/resources/dictionary/csv/spanish-swedish-verbs.csv";

    public CsvLoader() {
    }

    @Override
    public ArrayList<Vocable> loadWords(Language language1, Language language2, int numDesired) {
        Path pathToFile = Paths.get(filePath);
        ArrayList<Vocable> vocables = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8)) {
            String line = br.readLine();
            String[] header = line.split(",");
            Language columnOneLang = parseAndVerifyLanguage(header[0], language1, language2);
            Language columnTwoLang = parseAndVerifyLanguage(header[1], language1, language2);

            while (line != null && vocables.size() < numDesired) {
                String[] attributes = line.split(",");
                Vocable vocable = parseVocable(columnOneLang, columnTwoLang, attributes);
                vocables.add(vocable);
                line = br.readLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return vocables;
    }

    private Vocable parseVocable(Language columnOneLang, Language columnTwoLang, String[] row) {
        Word word1 = new Word(columnOneLang, row[0]);
        Word word2 = new Word(columnTwoLang, row[1]);
        return new Vocable(word1, word2);
    }

    private Language parseAndVerifyLanguage(@NotNull String csvCell, Language language1, Language language2) {
        Language csvLanguage = parseLanguage(csvCell);
        verifyLanguage(csvLanguage, language1, language2);
        return csvLanguage;
    }

    private Language parseLanguage(String csvCell) {
        try {
            return Language.valueOf(csvCell.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("Language in CSV not recognizable");
        }
    }

    private void verifyLanguage(Language csvLanguage, Language language1, Language language2) {
        if (csvLanguage != language1 && csvLanguage != language2) {
            throw new IllegalStateException(String.format("CSV language in header %s not matching one of %s or %s",
                    csvLanguage, language1, language2));
        }
    }
}
