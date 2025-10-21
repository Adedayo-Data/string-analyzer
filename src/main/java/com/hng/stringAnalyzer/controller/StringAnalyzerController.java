package com.hng.stringAnalyzer.controller;

import com.hng.stringAnalyzer.StringAnalyzerRepo;
import com.hng.stringAnalyzer.models.StringAnalyzer;
import com.hng.stringAnalyzer.services.StringAnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class StringAnalyzerController {

    private final StringAnalyzerService analyzerService;
    private final StringAnalyzerRepo analyzerRepo;

    @PostMapping("/strings")
    public ResponseEntity<StringAnalyzer> analyzeString(@RequestBody Map<String, Object> request){
        // Handling 400 bad request
        if(request.get("value") == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Handling 422 Unprocessable Entity
        Object objValue = request.get("value");
        if(!(objValue instanceof String)){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        // Handling 409 ERROR -> String already exist in the database
        String value = (String) objValue;
        List<String> dbValues = analyzerRepo.findByStringValue(value);
        if(dbValues.contains(value)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(analyzerService.stringAnalyzer(request));
    }

    // Get Specific String
    @GetMapping("/strings/{string_value}")
    public ResponseEntity<StringAnalyzer> fetchByValue(@PathVariable("string_value") String stringValue){
        StringAnalyzer analyzer = analyzerService.findByStringValue(stringValue);
        if(analyzer == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(analyzer);
    }

    // filter strings
    @GetMapping("/strings")
    public ResponseEntity<Map<String, Object>>  filterString(
            @RequestParam(required = false) Boolean is_palindrome,
            @RequestParam(required = false) Integer min_length,
            @RequestParam(required = false) Integer max_length,
            @RequestParam(required = false) Integer word_count,
            @RequestParam(required = false) String contains_character
    ){
        // fetch all strings in the database
        List<StringAnalyzer> allAnalyzedStrings = analyzerService.findAll();

        // convert them to stream for stream processing
        Stream<StringAnalyzer> stream = allAnalyzedStrings.stream();

        try{
            if(is_palindrome != null){
                stream = stream.filter(s -> analyzerService.is_palindrom(s.getValue()) == is_palindrome);
            }
            if(min_length != null){
                stream = stream.filter(s -> s.getValue().length() >= min_length);
            }
            if(max_length != null){
                stream = stream.filter(s -> s.getValue().length() <= max_length);
            }
            if(word_count != null){
                stream = stream.filter(s -> s.getValue().trim().split("\\s").length == word_count);
            }
            if(contains_character != null && contains_character.length() == 1){
                stream = stream.filter(s -> s.getValue().contains(contains_character)); // TO FIX
            }

            // Convert Streams to List
            List<StringAnalyzer> filtered = stream.toList();

            // build the json payload
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("Data", filtered);
            response.put("count", filtered.size());

            Map<String, Object> filters_applied = new LinkedHashMap<>();
            if(is_palindrome != null){
                filters_applied.put("is_palindrome", is_palindrome);
            }
            if(min_length != null){
                filters_applied.put("min_length", min_length);
            }
            if(max_length != null){
                filters_applied.put("max_length", max_length);
            }
            if(word_count != null){
                filters_applied.put("word_count", word_count);
            }
            if(contains_character != null && contains_character.length() == 1){
                filters_applied.put("contains_character", contains_character);
            }

            response.put("filters_applied", filters_applied);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("body", "invalid query parameter values or types"));
        }
    }

    // Delete String
    @DeleteMapping("/strings/{string_value}")
    public ResponseEntity<Void> deleteString(@PathVariable("string_value") String value){
        // find the string in the database
        StringAnalyzer string = analyzerRepo.findByValue(value);
        if(string == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        analyzerRepo.delete(string);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
