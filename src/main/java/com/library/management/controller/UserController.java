package com.library.management.controller;

import com.library.management.dto.response.UserResponse;
import com.library.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing library users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users with pagination and optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "User role filter") @RequestParam(required = false) String role,
            @Parameter(description = "Active users only") @RequestParam(required = false) Boolean active,
            @Parameter(hidden = true) Pageable pageable) {
        Page<UserResponse> response = userService.getAllUsers(role, active, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name or email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @Parameter(description = "Search query (name or email)") @RequestParam String query,
            @Parameter(hidden = true) Pageable pageable) {
        Page<UserResponse> response = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active-borrowers")
    @Operation(summary = "Get active borrowers", description = "Retrieves users who currently have borrowed books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active borrowers retrieved successfully")
    })
    public ResponseEntity<List<UserResponse>> getActiveBorrowers() {
        List<UserResponse> response = userService.getActiveBorrowers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-overdue")
    @Operation(summary = "Get users with overdue books", description = "Retrieves users who have overdue books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users with overdue books retrieved successfully")
    })
    public ResponseEntity<List<UserResponse>> getUsersWithOverdueBooks() {
        List<UserResponse> response = userService.getUsersWithOverdueBooks();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "Get user statistics", description = "Retrieves borrowing statistics for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User statistics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserStatistics(
            @Parameter(description = "User ID") @PathVariable Long id) {
        UserResponse response = userService.getUserStatistics(id);
        return ResponseEntity.ok(response);
    }
}

