package com.library.management.dto.response;

import com.library.management.entity.User;
import com.library.management.entity.UserRole;
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
@Schema(description = "Response DTO for user information")
public class UserResponse {
    
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;
    
    @Schema(description = "Username of the user", example = "johndoe")
    private String username;
    
    @Schema(description = "Name of the user", example = "John Doe")
    private String name;
    
    @Schema(description = "Email of the user", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "Role of the user", example = "User")
    private String role;
    
    @Schema(description = "Whether the user is active", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Number of books currently borrowed", example = "2")
    private Integer currentBorrowCount;
    
    @Schema(description = "Total number of books borrowed historically", example = "15")
    private Integer totalBorrowCount;
    
    @Schema(description = "Number of overdue books", example = "0")
    private Integer overdueCount;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}

