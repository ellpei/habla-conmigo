package com.habla.config;

import com.habla.domain.dictionary.CsvLoader;
import com.habla.service.DictionaryLoaderService;
import org.springframework.context.annotation.Bean;

public class DictionaryConfiguration {

    @Bean
    public CsvLoader getCsvLoader() {
        return new CsvLoader();
    }

    @Bean
    public DictionaryLoaderService getDictionaryLoaderAdapter(CsvLoader csvLoader) {
        return new DictionaryLoaderService(csvLoader);
    }
}
