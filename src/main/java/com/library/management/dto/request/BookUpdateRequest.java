package com.library.management.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.library.management.entity.Book;
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
@Schema(description = "Request DTO for updating a book")
public class BookUpdateRequest {
    
    @Size(min = 1, max = 255, message = "Book name must be between 1 and 255 characters")
    @Schema(description = "Name of the book", example = "The Great Gatsby - Updated Edition")
    private String name;
    
    @Size(max = 50000, message = "Short details must not exceed 50000 characters")
    @Schema(description = "Short details about the book", example = "A classic American novel - Updated")
    @JsonProperty("short_details")
    private String shortDetails;
    
    @Size(min = 1, max = 255, message = "Author name must be between 1 and 255 characters")
    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald")
    private String author;
    
    @Schema(description = "Detailed description about the book", example = "A story of decadence and excess in the Jazz Age - Updated")
    private String about;
    
    @Schema(description = "ID of the category this book belongs to", example = "1")
    private Long categoryId;
    
    @Schema(description = "Format of the book", example = "HARD_COPY")
    private Book.BookFormat format;
    
    @Min(value = 1, message = "Total copies must be at least 1")
    @Schema(description = "Total number of copies", example = "7")
    @JsonProperty("total_copies")
    private Integer totalCopies;
    
    @Min(value = 0, message = "Available copies cannot be negative")
    @Schema(description = "Number of available copies", example = "6")
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

//    private String book_cover;
//    private String book_coverUrl;
//
//    private String pdf_file;
//    private String pdf_fileUrl;
//
//    private String audio_file;
//    private String audio_fileUrl;
    @JsonProperty("book_cover_url")
    private String bookCoverUrl;
    @JsonProperty("pdf_file_url")
    private String pdfFileUrl;
    @JsonProperty("audio_file_url")
    private String audioFileUrl;
}

