package com.example.meow.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeKeywordsResult {
    private String filename;
    private String extractedText;
    private List<String> keywords;
    private String status;
    private String note;
}