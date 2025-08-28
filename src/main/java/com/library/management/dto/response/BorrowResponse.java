package com.library.management.dto.response;

import com.library.management.entity.Borrow;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO for borrow information")
public class BorrowResponse {
    
    @Schema(description = "Unique identifier of the borrow record", example = "1")
    private Long id;
    
    @Schema(description = "User information")
    private UserResponse user;
    
    @Schema(description = "Book information")
    private BookResponse book;
    
    @Schema(description = "Date when the book was borrowed", example = "2023-12-01")
    private LocalDate borrowDate;
    
    @Schema(description = "Due date for returning the book", example = "2023-12-15")
    private LocalDate dueDate;
    
    @Schema(description = "Date when the book was returned (null if not returned)", example = "2023-12-14")
    private LocalDate returnDate;
    
    @Schema(description = "Status of the borrow", example = "ACTIVE")
    private Borrow.BorrowStatus status;
    
    @Schema(description = "Number of times the borrow has been extended", example = "1")
    private Integer extensionCount;
    
    @Schema(description = "Whether the borrow is overdue", example = "false")
    private Boolean isOverdue;
    
    @Schema(description = "Number of days overdue (0 if not overdue)", example = "0")
    private Long daysOverdue;
    
    @Schema(description = "Whether the borrow can be extended", example = "true")
    private Boolean canBeExtended;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}

