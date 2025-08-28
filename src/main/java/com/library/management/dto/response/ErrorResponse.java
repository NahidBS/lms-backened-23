package com.library.management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Error response DTO")
public class ErrorResponse {
    
    @Schema(description = "HTTP status code", example = "404")
    private Integer status;
    
    @Schema(description = "Error message", example = "Resource not found")
    private String message;
    
    @Schema(description = "Detailed error description", example = "Book not found with id: 123")
    private String details;
    
    @Schema(description = "Request path where error occurred", example = "/api/books/123")
    private String path;
    
    @Schema(description = "Timestamp when error occurred")
    private LocalDateTime timestamp;
    
    @Schema(description = "List of validation errors (for validation failures)")
    private List<ValidationError> validationErrors;
    
    @Schema(description = "Map of validation errors (field -> error message)")
    private Map<String, String> validationErrorsMap;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Validation error details")
    public static class ValidationError {
        
        @Schema(description = "Field name that failed validation", example = "name")
        private String field;
        
        @Schema(description = "Validation error message", example = "Name is required")
        private String message;
        
        @Schema(description = "Rejected value", example = "")
        private Object rejectedValue;
    }
}

