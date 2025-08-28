package com.library.management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO for category information")
public class CategoryResponse {
    
    @Schema(description = "Unique identifier of the category", example = "1")
    private Long id;
    
    @Schema(description = "Name of the category", example = "Fiction")
    private String name;
    
    @Schema(description = "Description of the category", example = "Fictional books and novels")
    private String description;
    
    @Schema(description = "Number of books in this category", example = "25")
    private Integer bookCount;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}

