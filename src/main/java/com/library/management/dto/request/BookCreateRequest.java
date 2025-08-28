package com.library.management.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.library.management.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for creating a new book")
public class BookCreateRequest {
    
    @NotBlank(message = "Book name is required")
    @Size(max = 255, message = "Book name must be between 1 and 255 characters")
    @Schema(description = "Name of the book", example = "The Great Gatsby")
    private String name;
    
    @Size(max = 50000, message = "Short details must not exceed 50000 characters")
    @Schema(description = "Short details about the book", example = "A classic American novel")
    @JsonProperty("short_details")
    private String shortDetails;
    
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author name must be between 1 and 255 characters")
    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald")
    private String author;
    
    @Schema(description = "Detailed description about the book", example = "A story of decadence and excess in the Jazz Age")
    private String about;
    
    @NotNull(message = "Category ID is required")
    @Schema(description = "ID of the category this book belongs to", example = "1")
    @JsonProperty("categoryId")
    private Long categoryId;
    
    @NotNull(message = "Book format is required")
    @Schema(description = "Format of the book", example = "HARD_COPY")
    private Book.BookFormat format;
    
    @NotNull(message = "Total copies is required")
    @Min(value = 1, message = "Total copies must be at least 1")
    @Schema(description = "Total number of copies", example = "5")
    @JsonProperty("total_copies")
    private Integer totalCopies;
    
    @Schema(description = "Number of available copies (defaults to total copies if not provided)", example = "5")
    @JsonProperty("available_copies")
    private Integer availableCopies;
    
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    @Schema(description = "ISBN of the book", example = "9780743273565")
    private String isbn;
    
    @Min(value = 1000, message = "Publication year must be at least 1000")
    @Max(value = 2100, message = "Publication year must not exceed 2100")
    @Schema(description = "Publication year of the book", example = "1925")
    @JsonProperty("publication_year")
    private Integer publicationYear;

<<<<<<< HEAD
    // The book cover URL is set internally after file upload; it should not be provided by clients.
    @Schema(hidden = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
=======
    @Schema(description = "URL for book cover image", example = "https://example.com/covers/great-gatsby.jpg")
    @JsonProperty("book_cover_url")
>>>>>>> upstream/main
    private String bookCoverUrl;

    @Schema(description = "URL for PDF version of the book", example = "https://example.com/pdfs/great-gatsby.pdf")
    @JsonProperty("pdf_file_url")
    private String pdfFileUrl;

    @Schema(description = "URL for audio version of the book", example = "https://example.com/audio/great-gatsby.mp3")
    @JsonProperty("audio_file_url")
    private String audioFileUrl;

}

