package com.example.meow.extractor;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeExtractor {
    boolean supports(String extension);
    String extractText(MultipartFile file) throws Exception;
}