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
@Schema(description = "Response DTO for review information")
public class ReviewResponse {
    
    @Schema(description = "Review ID", example = "1")
    private Long id;
    
    @Schema(description = "User information")
    private UserResponse user;
    
    @Schema(description = "Book information")
    private BookResponse book;
    
    @Schema(description = "Rating given to the book (1-5 stars)", example = "4")
    private Integer rating;
    
    @Schema(description = "Comment about the book", example = "Great book with excellent character development!")
    private String comment;
    
    @Schema(description = "Whether the review can be edited by the current user", example = "true")
    private Boolean canBeEdited;
    
    @Schema(description = "Whether the review can be deleted by the current user", example = "true")
    private Boolean canBeDeleted;
    
    @Schema(description = "Timestamp when the review was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the review was last updated", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}

