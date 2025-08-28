package com.library.management.dto.response;

import com.library.management.entity.Booking;
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
@Schema(description = "Response DTO for booking information")
public class BookingResponse {
    
    @Schema(description = "Booking ID", example = "1")
    private Long id;
    
    @Schema(description = "User information")
    private UserResponse user;
    
    @Schema(description = "Book information")
    private BookResponse book;
    
    @Schema(description = "Date when the booking was made", example = "2024-01-15")
    private LocalDate bookingDate;
    
    @Schema(description = "Expected date when the book will be available", example = "2024-02-15")
    private LocalDate expectedAvailableDate;
    
    @Schema(description = "Current status of the booking", example = "PENDING")
    private Booking.BookingStatus status;
    
    @Schema(description = "Number of days until expected availability", example = "5")
    private Long daysUntilAvailable;
    
    @Schema(description = "Whether the booking can be cancelled", example = "true")
    private Boolean canBeCancelled;
    
    @Schema(description = "Timestamp when the booking was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the booking was last updated", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}

