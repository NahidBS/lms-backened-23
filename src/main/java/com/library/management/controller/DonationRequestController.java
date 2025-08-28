package com.library.management.controller;

import com.library.management.dto.request.DonationRequestCreateRequest;
import com.library.management.dto.request.DonationRequestUpdateRequest;
import com.library.management.dto.request.DonationStatusUpdateRequest;
import com.library.management.dto.response.DonationRequestResponse;
import com.library.management.service.DonationRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donation-req")
@RequiredArgsConstructor
@Tag(name = "Donation Request Management", description = "APIs for managing book donation requests")
public class DonationRequestController {

    private final DonationRequestService donationRequestService;

    @PostMapping("/create")
    @Operation(summary = "Create a new donation request", description = "Creates a new book donation request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Donation request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DonationRequestResponse> createDonationRequest(
            @Valid @RequestBody DonationRequestCreateRequest request) {
        DonationRequestResponse response = donationRequestService.createDonationRequest(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all donation requests", description = "Retrieves all donation requests with pagination and optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donation requests retrieved successfully")
    })
    public ResponseEntity<Page<DonationRequestResponse>> getAllDonationRequests(
            @Parameter(description = "User ID filter") @RequestParam(required = false) Long userId,
            @Parameter(description = "Status filter") @RequestParam(required = false) String status,
            @Parameter(hidden = true) Pageable pageable) {
        Page<DonationRequestResponse> response = donationRequestService.getAllDonationRequests(userId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/retrieve/{id}")
    @Operation(summary = "Retrieve donation request details", description = "Retrieves detailed information about a donation request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donation request details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Donation request not found")
    })
    public ResponseEntity<DonationRequestResponse> retrieveDonationRequest(
            @Parameter(description = "Donation request ID") @PathVariable Long id) {
        DonationRequestResponse response = donationRequestService.getDonationRequestById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "Update donation request", description = "Updates an existing donation request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donation request updated successfully"),
            @ApiResponse(responseCode = "404", description = "Donation request not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or request cannot be updated")
    })
    public ResponseEntity<DonationRequestResponse> updateDonationRequest(
            @Parameter(description = "Donation request ID") @PathVariable Long id,
            @Valid @RequestBody DonationRequestUpdateRequest request) {
        DonationRequestResponse response = donationRequestService.updateDonationRequest(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete donation request", description = "Deletes a donation request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Donation request deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Donation request not found"),
            @ApiResponse(responseCode = "400", description = "Donation request cannot be deleted")
    })
    public ResponseEntity<Void> deleteDonationRequest(
            @Parameter(description = "Donation request ID") @PathVariable Long id) {
        donationRequestService.deleteDonationRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/status/{id}")
    @Operation(summary = "Update donation request status", description = "Updates the status of a donation request (false/0 = reject, true/1 = approve)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donation request status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Donation request not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public ResponseEntity<DonationRequestResponse> updateDonationRequestStatus(
            @Parameter(description = "Donation request ID") @PathVariable Long id,
            @Valid @RequestBody DonationStatusUpdateRequest request) {
        DonationRequestResponse response = donationRequestService.updateDonationRequestStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's donation requests", description = "Retrieves all donation requests for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User donation requests retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<DonationRequestResponse>> getUserDonationRequests(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Status filter") @RequestParam(required = false) String status) {
        List<DonationRequestResponse> response = donationRequestService.getUserDonationRequests(userId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending donation requests", description = "Retrieves all pending donation requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending donation requests retrieved successfully")
    })
    public ResponseEntity<List<DonationRequestResponse>> getPendingDonationRequests() {
        List<DonationRequestResponse> response = donationRequestService.getPendingDonationRequests();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approved")
    @Operation(summary = "Get approved donation requests", description = "Retrieves all approved donation requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Approved donation requests retrieved successfully")
    })
    public ResponseEntity<List<DonationRequestResponse>> getApprovedDonationRequests() {
        List<DonationRequestResponse> response = donationRequestService.getApprovedDonationRequests();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve/{id}")
    @Operation(summary = "Approve donation request", description = "Admin approves a donation request by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donation request approved successfully"),
            @ApiResponse(responseCode = "404", description = "Donation request not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request or donation request cannot be approved")
    })
    public ResponseEntity<DonationRequestResponse> approveDonationRequest(
            @Parameter(description = "Donation request ID") @PathVariable Long id,
            @Parameter(description = "Admin notes") @RequestParam(required = false) String adminNotes) {
        DonationRequestResponse response = donationRequestService.approveDonationRequest(id, adminNotes);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reject/{id}")
    @Operation(summary = "Reject donation request", description = "Admin rejects a donation request by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donation request rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Donation request not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request or donation request cannot be rejected")
    })
    public ResponseEntity<DonationRequestResponse> rejectDonationRequest(
            @Parameter(description = "Donation request ID") @PathVariable Long id,
            @Parameter(description = "Admin notes") @RequestParam(required = false) String adminNotes) {
        DonationRequestResponse response = donationRequestService.rejectDonationRequest(id, adminNotes);
        return ResponseEntity.ok(response);
    }
}

