package com.example.meow.service;

import com.example.meow.extractor.ResumeExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public class ResumeExtractionManager {

    private final List<ResumeExtractor> extractors;

    public ResumeExtractionManager(List<ResumeExtractor> extractors) {
        this.extractors = extractors;
    }

    public String extract(MultipartFile file) throws Exception {
        String ext = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

        for (ResumeExtractor extractor : extractors) {
            if (extractor.supports(ext)) {
                return extractor.extractText(file);
            }
        }

        throw new IllegalArgumentException("Unsupported file type: " + ext);
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }
}