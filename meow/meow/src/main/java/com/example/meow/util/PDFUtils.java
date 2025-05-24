package com.example.meow.util;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfWriter;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PDFUtils {

    private static final Logger logger = LoggerFactory.getLogger(PDFUtils.class);
    private static final String TESSDATA_PATH = "C:\\Program Files\\Tesseract-OCR\\tessdata";

    // 取得檔案大小
    public static double getFileSizeInMB(MultipartFile file) {
        return file.getSize() / (1024.0 * 1024.0);
    }

    // PDFBox 提取 PDF 純文字
    public static String extractWithPdfBox(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getInputStream().readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            logger.error("Failed to extract text with PDFBox", e);
            return "";
        }
    }

    public static String extractWithOCR(MultipartFile file) {
        StringBuilder result = new StringBuilder();

        if (file == null || file.isEmpty()) {
            logger.error("OCR failed: uploaded file is null or empty.");
            return "";
        }

        try (PDDocument document = Loader.loadPDF(file.getInputStream().readAllBytes())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            Tesseract tesseract = new Tesseract();

            // 指定 tesseract 的語言包路徑與語言
            tesseract.setDatapath(TESSDATA_PATH); // ✅ 請依系統路徑自行調整
            tesseract.setLanguage("eng+chi_tra");

            int pageCount = document.getNumberOfPages();
            logger.info("Performing OCR on {} pages...", pageCount);

            for (int page = 0; page < pageCount; ++page) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300);

                if (image == null || image.getWidth() == 0 || image.getHeight() == 0) {
                    logger.warn("Skipped page {}: image is null or empty", page);
                    continue;
                }

                try {
                    String text = tesseract.doOCR(image);
                    result.append(text).append("\n");
                    logger.debug("OCR extracted {} characters from page {}", text.length(), page);
                } catch (TesseractException te) {
                    logger.error("OCR failed on page {}", page, te);
                }
            }

        } catch (IOException e) {
            logger.error("Failed to perform OCR on PDF", e);
        }

        return result.toString();
    }

    // OpenPDF 檔案壓縮
    public static MultipartFile compressPdf(MultipartFile file) {
        try {
            // IO Stream
            InputStream input = file.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            // reader & stamper 壓縮處理器
            PdfReader reader = new PdfReader(input);
            PdfStamper stamper = new PdfStamper(reader, output, PdfWriter.VERSION_1_5);

            // 壓縮模式
            stamper.getWriter().setCompressionLevel(PdfStream.BEST_COMPRESSION);
            stamper.setFullCompression();

            stamper.close();
            reader.close();

            // 將壓縮後的 ByteArray 轉回 MultipartFile
            return new InMemoryMultipartFile(
                    file.getName(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    output.toByteArray()
            );
        } catch (IOException e) {
            logger.error("Failed to compress PDF", e);
            return file;
        }
    }
}
