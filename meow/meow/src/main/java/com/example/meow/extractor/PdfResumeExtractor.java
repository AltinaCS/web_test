package com.example.meow.extractor;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.example.meow.util.PDFUtils.*;

@Component
public class PdfResumeExtractor implements com.example.meow.extractor.ResumeExtractor {

    @Override
    public boolean supports(String extension) {
        return extension.equalsIgnoreCase(".pdf");
    }

    @Override
    public String extractText(MultipartFile file) throws Exception {
        MultipartFile compressedFile = file;

        if (getFileSizeInMB(file) > 10) {
            compressedFile = compressPdf(file);
        }

        String text = extractWithPdfBox(compressedFile);

        if (text == null || text.length() < 50) {
            text = extractWithOCR(compressedFile);
        }

        return text;
    }
}
