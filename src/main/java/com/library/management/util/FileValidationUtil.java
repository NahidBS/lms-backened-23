package com.library.management.util;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileValidationUtil {
    
    private static final Tika tika = new Tika();
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png");
    private static final List<String> ALLOWED_PDF_TYPES = Arrays.asList("application/pdf");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    public static boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        try {
            String mimeType = tika.detect(file.getInputStream());
            return ALLOWED_IMAGE_TYPES.contains(mimeType) && file.getSize() <= MAX_FILE_SIZE;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static boolean isValidPdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        try {
            String mimeType = tika.detect(file.getInputStream());
            return ALLOWED_PDF_TYPES.contains(mimeType) && file.getSize() <= MAX_FILE_SIZE;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}

