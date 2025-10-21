package com.hng.stringAnalyzer.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.HashMap;
import java.util.Map;

@Converter
public class CharacterFreq implements AttributeConverter<Map<Character, Integer>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<Character, Integer> map) {
        try {
            // Convert Character keys to String before saving
            Map<String, Integer> stringKeyMap = new HashMap<>();
            for (Map.Entry<Character, Integer> entry : map.entrySet()) {
                stringKeyMap.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            return mapper.writeValueAsString(stringKeyMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Character, Integer> convertToEntityAttribute(String json) {
        try {
            // Read as Map<String, Integer> first
            Map<String, Integer> stringKeyMap = mapper.readValue(json, new TypeReference<>() {
            });
            // Convert String keys back to Character
            Map<Character, Integer> charKeyMap = new HashMap<>();
            for (Map.Entry<String, Integer> entry : stringKeyMap.entrySet()) {
                if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                    charKeyMap.put(entry.getKey().charAt(0), entry.getValue());
                }
            }
            return charKeyMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
