package com.hng.stringAnalyzer.controller;

import com.hng.stringAnalyzer.models.StringAnalyzer;
import com.hng.stringAnalyzer.services.StringAnalyzerService;
import com.hng.stringAnalyzer.utils.NaturalLanguageParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/strings")
public class StringNaturalLanguageController {

    private final StringAnalyzerService stringService;
    private final NaturalLanguageParser parser;

    public StringNaturalLanguageController(StringAnalyzerService stringService, NaturalLanguageParser parser) {
        this.stringService = stringService;
        this.parser = parser;
    }

    @GetMapping("/filter-by-natural-language")
    public ResponseEntity<Map<String, Object>> filterByNaturalLanguage(@RequestParam String query) {
        try {
            Map<String, Object> parsedFilters = parser.parseQuery(query);

            if (parsedFilters == null || parsedFilters.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Unable to parse natural language query"));
            }

            if (parsedFilters.containsKey("conflict")) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(Map.of("error", "Query parsed but resulted in conflicting filters"));
            }

            List<StringAnalyzer> filtered = stringService.filterStrings(
                    (Boolean) parsedFilters.get("is_palindrome"),
                    (Integer) parsedFilters.get("min_length"),
                    (Integer) parsedFilters.get("max_length"),
                    (Integer) parsedFilters.get("word_count"),
                    (String) parsedFilters.get("contains_character")
            );

            Map<String, Object> interpreted = Map.of(
                    "original", query,
                    "parsed_filters", parsedFilters
            );

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("data", filtered);
            response.put("count", filtered.size());
            response.put("interpreted_query", interpreted);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Unable to parse natural language query"));
        }
    }
}
