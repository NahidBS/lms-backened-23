package com.library.management.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for updating a donation request")
public class DonationRequestUpdateRequest {
    
    @NotBlank(message = "Book title is required")
    @Size(max = 255, message = "Book title must not exceed 255 characters")
    @Schema(description = "Title of the book being donated", example = "The Great Gatsby")
    @JsonProperty("bookTitle")
    private String bookTitle;
    
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    @Schema(description = "Author of the book being donated", example = "F. Scott Fitzgerald")
    private String author;
    
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    @Schema(description = "ISBN of the book (optional)", example = "978-0-7432-7356-5")
    private String isbn;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Description of the book and its condition", example = "Hardcover book in excellent condition, no markings or damage")
    private String description;
}

