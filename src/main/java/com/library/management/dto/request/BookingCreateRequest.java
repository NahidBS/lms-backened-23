package com.library.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for creating a new booking")
public class BookingCreateRequest {
    
    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user making the booking", example = "1")
    private Long userId;
    
    @NotNull(message = "Book ID is required")
    @Schema(description = "ID of the book being booked", example = "1")
    private Long bookId;
    
    @Schema(description = "Expected available date for the book (optional, system will calculate if not provided)", example = "2024-02-15")
    private LocalDate expectedAvailableDate;
}

