package com.example.meow.extractor;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Component
public class TextResumeExtractor implements com.example.meow.extractor.ResumeExtractor {

    @Override
    public boolean supports(String extension) {
        return extension.equalsIgnoreCase(".txt");
    }

    @Override
    public String extractText(MultipartFile file) throws Exception {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }
}
