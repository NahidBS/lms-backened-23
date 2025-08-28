package com.library.management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response DTO for admin settings")
public class AdminSettingResponse {
    
    @Schema(description = "Maximum number of days a book can be borrowed", example = "14")
    private Integer borrowDayLimit;
    
    @Schema(description = "Maximum number of times a borrow can be extended", example = "2")
    private Integer borrowExtendLimit;
    
    @Schema(description = "Maximum number of books a user can borrow simultaneously", example = "5")
    private Integer borrowBookLimit;
    
    @Schema(description = "Maximum number of days in advance a book can be booked", example = "7")
    private Integer bookingDaysLimit;
}

