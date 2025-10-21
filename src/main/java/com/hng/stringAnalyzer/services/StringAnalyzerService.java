package com.hng.stringAnalyzer.services;

import com.hng.stringAnalyzer.StringAnalyzerRepo;
import com.hng.stringAnalyzer.models.Properties;
import com.hng.stringAnalyzer.models.StringAnalyzer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StringAnalyzerService {

    private final StringAnalyzerRepo analyzerRepo;

    // analyzer string
    public StringAnalyzer stringAnalyzer(Map<String, Object> request){

        Object objValue = request.get("value");
        String value = (String) objValue;

        StringAnalyzer analyzer = new StringAnalyzer();

        // Set Values for StringAnalyzer
        analyzer.setId(generateHashId());
        analyzer.setValue(value);

        // Setting Properties values
        Properties prop = new Properties();
        prop.setLength(StrLen(value));
        prop.set_palindrome(is_palindrom(value));
        prop.setUnique_characters(countUniqueCharacters(value));
        prop.setWord_count(wordCount(value));
        prop.setSha256_hash(sha265Encrypt(value));
        prop.setCharacter_frequency_map(characterFrequency(value));

        // Setting values for String Analyzer
        analyzer.setProperties(prop);
        analyzer.setCreated_at(Instant.now());

        // save Analyzer Report

        return analyzerRepo.save(analyzer);
    }

    // Compute the length of the String
    public int StrLen(String str){
        return str.length();
    }

    // Check if String is palindrome
    public Boolean is_palindrom(String str){
        String cleanStr = str.replaceAll("\\s", "").toLowerCase();
        StringBuilder sb = new StringBuilder(cleanStr);
        return cleanStr.contentEquals(sb.reverse());
    }

    // unique Characters
    public int countUniqueCharacters(String str){
        String cleanStr = str.replaceAll("\\s", "");
        Set<Character> characterSet = new HashSet<>();

        for(char c : cleanStr.toCharArray()){
            characterSet.add(c);
        }
        return characterSet.size();
    }

    // word count
    public int wordCount(String str){
        String[] cleanStr = str.trim().split("\\s");
        return cleanStr.length;
    }

    // Encrypt with Sha256_hash
    public String sha265Encrypt(String str){

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);

        }catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Create a character freq. count
    public Map<Character, Integer> characterFrequency(String str){
        Map<Character, Integer> charFreq = new HashMap<>();

        for(char c : str.toCharArray()){
            if(c != ' '){
                charFreq.put(c, charFreq.getOrDefault(c, 0)+1);
            }
        }
        return charFreq;
    }

    // Generate Hash id
    public String generateHashId(){
        String uuid = UUID.randomUUID().toString();

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] byteArr = md.digest(uuid.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(byteArr);
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    // find by String value
    public StringAnalyzer findByStringValue(String value){
        return analyzerRepo.findByValue(value);
    }

    // find all
    public List<StringAnalyzer> findAll(){
        return analyzerRepo.findAll();
    }


    // util method
    public List<StringAnalyzer> filterStrings(
            Boolean is_palindrome,
            Integer min_length,
            Integer max_length,
            Integer word_count,
            String contains_character
    ) {

        List<StringAnalyzer> strings = analyzerRepo.findAll();

        Stream<StringAnalyzer> stream = strings.stream();

        // ✅ Filter 1: Palindrome
        if (is_palindrome != null) {
            stream = stream.filter(s -> is_palindrom(s.getValue()) == is_palindrome);
        }

        // ✅ Filter 2: Minimum length
        if (min_length != null) {
            stream = stream.filter(s -> s.getValue().length() >= min_length);
        }

        // ✅ Filter 3: Maximum length
        if (max_length != null) {
            stream = stream.filter(s -> s.getValue().length() <= max_length);
        }

        // ✅ Filter 4: Word count (split by spaces)
        if (word_count != null) {
            stream = stream.filter(s -> s.getValue().trim().split("\\s+").length == word_count);
        }

        // ✅ Filter 5: Contains specific character
        if (contains_character != null && !contains_character.isEmpty()) {
            char ch = contains_character.charAt(0);
            stream = stream.filter(s -> s.getValue().toLowerCase().indexOf(ch) != -1);
        }

        return stream.collect(Collectors.toList());
    }
}
