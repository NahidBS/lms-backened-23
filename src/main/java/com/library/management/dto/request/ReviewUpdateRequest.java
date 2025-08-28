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
@Schema(description = "Request DTO for updating a review")
public class ReviewUpdateRequest {
    
    @NotNull(message = "User ID is required for authorization")
    @Schema(description = "ID of the user updating the review (for authorization)", example = "1")
    @JsonProperty("userId")
    private Long userId;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Schema(description = "Updated rating for the book (1-5 stars)", example = "5")
    private Integer rating;
    
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    @Schema(description = "Updated comment about the book", example = "Even better on second reading!")
    private String comment;
}

