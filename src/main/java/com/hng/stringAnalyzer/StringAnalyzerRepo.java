package com.hng.stringAnalyzer;

import com.hng.stringAnalyzer.models.StringAnalyzer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StringAnalyzerRepo extends JpaRepository<StringAnalyzer, String> {

    @Query("SELECT s.value FROM StringAnalyzer s WHERE s.value = :value")
    List<String> findByStringValue(@Param("value") String value);

    StringAnalyzer findByValue(String value);
}
