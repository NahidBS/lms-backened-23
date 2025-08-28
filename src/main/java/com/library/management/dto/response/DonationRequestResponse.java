package com.library.management.dto.response;

import com.library.management.entity.DonationRequest;
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
@Schema(description = "Response DTO for donation request information")
public class DonationRequestResponse {
    
    @Schema(description = "Donation request ID", example = "1")
    private Long id;
    
    @Schema(description = "User information")
    private UserResponse user;
    
    @Schema(description = "Title of the book being donated", example = "The Great Gatsby")
    private String bookTitle;
    
    @Schema(description = "Author of the book being donated", example = "F. Scott Fitzgerald")
    private String author;
    
    @Schema(description = "ISBN of the book", example = "978-0-7432-7356-5")
    private String isbn;
    
    @Schema(description = "Description of the book and its condition", example = "Hardcover book in excellent condition")
    private String description;
    
    @Schema(description = "Current status of the donation request", example = "PENDING")
    private DonationRequest.DonationStatus status;
    
    @Schema(description = "Admin notes about the request", example = "Book approved for addition to library collection")
    private String adminNotes;
    
    @Schema(description = "Whether the request can be edited", example = "true")
    private Boolean canBeEdited;
    
    @Schema(description = "Whether the request can be deleted", example = "true")
    private Boolean canBeDeleted;
    
    @Schema(description = "Timestamp when the request was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the request was last updated", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}

