package com.library.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for admin setting")
public class AdminSettingRequest {
    
    @NotNull(message = "Value is required")
    @Min(value = 1, message = "Value must be at least 1")
    @Schema(description = "Setting value", example = "14")
    private Integer value;
}

