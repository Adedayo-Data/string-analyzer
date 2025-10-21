package com.hng.stringAnalyzer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Embeddable
@Setter
@Getter
public class Properties {

    private int length;

    @Getter(onMethod = @__(@JsonProperty("is_palindrome")))
    private boolean is_palindrome;
    private int unique_characters;
    private int word_count;
    private String sha256_hash;

    @Convert(converter = CharacterFreq.class)
    private Map<Character, Integer> character_frequency_map;
}
