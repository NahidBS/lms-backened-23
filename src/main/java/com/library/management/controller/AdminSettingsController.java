package com.library.management.controller;

import com.library.management.dto.request.AdminSettingRequest;
import com.library.management.dto.response.AdminSettingResponse;
import com.library.management.service.AdminSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Admin Settings", description = "APIs for managing admin settings")
public class AdminSettingsController {

    private final AdminSettingsService adminSettingsService;

    @PostMapping("/borrow-day-limit")
    @Operation(summary = "Set borrow day limit", description = "Sets the maximum number of days a book can be borrowed (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrow day limit updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<AdminSettingResponse> setBorrowDayLimit(
            @Valid @RequestBody AdminSettingRequest request) {
        AdminSettingResponse response = adminSettingsService.setBorrowDayLimit(request.getValue());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/borrow-extend-limit")
    @Operation(summary = "Set borrow extend limit", description = "Sets the maximum number of times a borrow can be extended (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrow extend limit updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<AdminSettingResponse> setBorrowExtendLimit(
            @Valid @RequestBody AdminSettingRequest request) {
        AdminSettingResponse response = adminSettingsService.setBorrowExtendLimit(request.getValue());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/borrow-book-limit")
    @Operation(summary = "Set borrow book limit", description = "Sets the maximum number of books a user can borrow simultaneously (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrow book limit updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<AdminSettingResponse> setBorrowBookLimit(
            @Valid @RequestBody AdminSettingRequest request) {
        AdminSettingResponse response = adminSettingsService.setBorrowBookLimit(request.getValue());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/booking-days-limit")
    @Operation(summary = "Set booking days limit", description = "Sets the maximum number of days in advance a book can be booked (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking days limit updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<AdminSettingResponse> setBookingDaysLimit(
            @Valid @RequestBody AdminSettingRequest request) {
        AdminSettingResponse response = adminSettingsService.setBookingDaysLimit(request.getValue());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin-settings")
    @Operation(summary = "Get all admin settings", description = "Retrieves all admin settings (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin settings retrieved successfully")
    })
    public ResponseEntity<AdminSettingResponse> getAllAdminSettings() {
        AdminSettingResponse response = adminSettingsService.getAllSettings();
        return ResponseEntity.ok(response);
    }
}

