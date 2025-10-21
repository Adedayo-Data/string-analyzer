package com.hng.stringAnalyzer.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class StringAnalyzer {

    @Id
    private String id;

    @Column(name = "`value`")
    private String value;

    @Embedded
    private Properties properties;
    private Instant created_at;
}
