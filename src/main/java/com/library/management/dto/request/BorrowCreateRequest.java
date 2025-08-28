package com.library.management.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for creating a new borrow record")
public class BorrowCreateRequest {
    
    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user borrowing the book", example = "1")
    @JsonProperty("userId")
    private Long userId;
    
    @NotNull(message = "Book ID is required")
    @Schema(description = "ID of the book being borrowed", example = "1")
    @JsonProperty("bookId")
    private Long bookId;
}

