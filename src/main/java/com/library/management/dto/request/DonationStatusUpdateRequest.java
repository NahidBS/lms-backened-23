package com.library.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for updating donation request status")
public class DonationStatusUpdateRequest {
    
    @NotNull(message = "Status is required")
    @Schema(description = "Status value: false/0 = reject, true/1 = approve", example = "true")
    private Boolean status;
    
    @Size(max = 500, message = "Admin notes must not exceed 500 characters")
    @Schema(description = "Optional admin notes about the status change", example = "Book approved for addition to library collection")
    private String adminNotes;
}

