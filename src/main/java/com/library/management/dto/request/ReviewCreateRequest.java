package com.library.management.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for creating a new review")
public class ReviewCreateRequest {
    
    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user creating the review", example = "1")
    @JsonProperty("userId")
    private Long userId;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Schema(description = "Rating for the book (1-5 stars)", example = "4")
    private Integer rating;
    
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    @Schema(description = "Optional comment about the book", example = "Great book with excellent character development!")
    private String comment;
}

