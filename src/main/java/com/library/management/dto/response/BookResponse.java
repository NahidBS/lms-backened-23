package com.library.management.dto.response;

import com.library.management.entity.Book;
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
@Schema(description = "Response DTO for book information")
public class BookResponse {
    
    @Schema(description = "Unique identifier of the book", example = "1")
    private Long id;
    
    @Schema(description = "Name of the book", example = "The Great Gatsby")
    private String name;
    
    @Schema(description = "Short details about the book", example = "A classic American novel")
    private String shortDetails;
    
    @Schema(description = "Author of the book", example = "F. Scott Fitzgerald")
    private String author;
    
    @Schema(description = "Detailed description about the book", example = "A story of decadence and excess in the Jazz Age")
    private String about;
    
    @Schema(description = "Category information")
    private CategoryResponse category;
    
    @Schema(description = "Format of the book", example = "HARD_COPY")
    private Book.BookFormat format;
    
    @Schema(description = "Total number of copies", example = "5")
    private Integer totalCopies;
    
    @Schema(description = "Number of available copies", example = "3")
    private Integer availableCopies;
    
    @Schema(description = "ISBN of the book", example = "9780743273565")
    private String isbn;
    
    @Schema(description = "Publication year of the book", example = "1925")
    private Integer publicationYear;
    
    @Schema(description = "Average rating of the book", example = "4.5")
    private Double averageRating;
    
    @Schema(description = "Number of reviews for the book", example = "10")
    private Integer reviewCount;
    
    @Schema(description = "Whether the book is currently available", example = "true")
    private Boolean isAvailable;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
//
//    private String book_cover;
//    private String pdf_file;
//    private String audio_file;

    private String bookCoverUrl;
    private String pdfFileUrl;
    private String audioFileUrl;
}

