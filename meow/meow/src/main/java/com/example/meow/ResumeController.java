package com.example.meow;

import com.example.meow.service.ResumeExtractionManager;
import com.example.meow.service.ResumeKeywordsResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeExtractionManager extractionManager;

    public ResumeController(ResumeExtractionManager extractionManager) {
        this.extractionManager = extractionManager;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResumeKeywordsResult> uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            String text = extractionManager.extract(file);
            ResumeKeywordsResult result = ResumeKeywordsResult.builder()
                    .filename(file.getOriginalFilename())
                    .extractedText(GeminiAPIController.callGeminiAPI(text))
                    .keywords(List.of())
                    .status("success")
                    .note("Keywords:")//TODO:之後加字
                    .build();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Resume parsing failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}