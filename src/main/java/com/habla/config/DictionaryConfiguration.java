package com.habla.config;

import com.habla.domain.dictionary.CsvLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DictionaryConfiguration {

    @Bean
    public CsvLoader getCsvLoader() {
        return new CsvLoader();
    }
}
