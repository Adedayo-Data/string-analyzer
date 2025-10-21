package com.hng.stringAnalyzer.utils;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NaturalLanguageParser {

    public Map<String, Object> parseQuery(String query) {
        if (query == null || query.isBlank()) return null;

        String q = query.toLowerCase();
        Map<String, Object> filters = new LinkedHashMap<>();

        // Handle palindromic
        if (q.contains("palindromic") || q.contains("palindrome")) {
            filters.put("is_palindrome", true);
        }

        // Handle "single word" or "one word"
        if (q.contains("single word") || q.contains("one word")) {
            filters.put("word_count", 1);
        }

        // Handle "strings longer than X characters"
        Matcher longerMatcher = Pattern.compile("longer than (\\d+)").matcher(q);
        if (longerMatcher.find()) {
            int num = Integer.parseInt(longerMatcher.group(1));
            filters.put("min_length", num + 1);
        }

        // Handle "shorter than X characters"
        Matcher shorterMatcher = Pattern.compile("shorter than (\\d+)").matcher(q);
        if (shorterMatcher.find()) {
            int num = Integer.parseInt(shorterMatcher.group(1));
            filters.put("max_length", num - 1);
        }

        // Handle "exactly X characters"
        Matcher exactMatcher = Pattern.compile("exactly (\\d+)").matcher(q);
        if (exactMatcher.find()) {
            int num = Integer.parseInt(exactMatcher.group(1));
            filters.put("min_length", num);
            filters.put("max_length", num);
        }

        // Handle "containing the letter X" or "contains the letter X"
        Matcher containMatcher = Pattern.compile("containing the letter ([a-z])").matcher(q);
        if (containMatcher.find()) {
            filters.put("contains_character", containMatcher.group(1));
        } else {
            Matcher containSimple = Pattern.compile("contain[s]* ([a-z])").matcher(q);
            if (containSimple.find()) {
                filters.put("contains_character", containSimple.group(1));
            }
        }

        // Handle "first vowel"
        if (q.contains("first vowel")) {
            filters.put("contains_character", "a"); // heuristic choice
        }

        // Detect conflicts (optional example)
        if (q.contains("palindromic") && q.contains("non-palindromic")) {
            filters.put("conflict", true);
        }

        return filters;
    }
}
