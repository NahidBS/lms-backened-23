package com.library.management.controller;

import com.library.management.dto.request.BorrowCreateRequest;
import com.library.management.dto.response.BorrowResponse;
import com.library.management.service.BorrowService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
@Tag(name = "Borrow Management", description = "APIs for managing book borrowing and returns")
public class BorrowController {

    private final BorrowService borrowService;

    @GetMapping("/stats")
    @Tag(name = "Borrow Statistics", description = "APIs for getting statistics of borrowed books")
    @Operation(summary = "Get borrow statistics",
            description = "Returns total counts of borrowed, returned, overdue, requests, and rejects")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<Map<String, Long>> getBorrowStats() {
        long totalBorrows   = borrowService.countTotalBorrows();
        long borrowedCount  = borrowService.countActiveBorrows();
        long returnedCount  = borrowService.countReturnedBorrows();
        long overdueCount   = borrowService.countOverdueBorrows();
        long requestCount   = borrowService.countRequestedBorrows();
        long rejectCount    = borrowService.countRejectedBorrows();

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalBorrowed", totalBorrows);
        stats.put("borrowedBooks", borrowedCount);
        stats.put("returnedBooks", returnedCount);
        stats.put("overdueBooks", overdueCount);
        stats.put("totalRequest", requestCount);
        stats.put("totalReject", rejectCount);

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/create")
    @Operation(summary = "Borrow a book", description = "Creates a new book borrow record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book borrowed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "409", description = "Book not available or user has reached borrowing limit")
    })
    public ResponseEntity<BorrowResponse> borrowBook(
            @Valid @RequestBody BorrowCreateRequest request) {
        BorrowResponse response = borrowService.borrowBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/retrieve/{id}")
    @Operation(summary = "Retrieve borrow details", description = "Retrieves detailed information about a borrow record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrow details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Borrow record not found")
    })
    public ResponseEntity<BorrowResponse> retrieveBorrow(
            @Parameter(description = "Borrow ID") @PathVariable Long id) {
        BorrowResponse response = borrowService.getBorrowById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all borrows", description = "Retrieves all borrow records with pagination and optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrow records retrieved successfully")
    })
    public ResponseEntity<Page<BorrowResponse>> getAllBorrows(
            @Parameter(description = "User ID filter") @RequestParam(required = false) Long userId,
            @Parameter(description = "Book ID filter") @RequestParam(required = false) Long bookId,
            @Parameter(description = "Active borrows only") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Overdue borrows only") @RequestParam(required = false) Boolean overdue,
            @Parameter(hidden = true) Pageable pageable) {
        Page<BorrowResponse> response = borrowService.getAllBorrows(userId, bookId, active, overdue, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's borrows", description = "Retrieves all borrow records for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User borrows retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Page<BorrowResponse>> getUserBorrows(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Active borrows only") @RequestParam(required = false) Boolean active,
            @Parameter(hidden = true) Pageable pageable) {
        Page<BorrowResponse> response = borrowService.getUserBorrows(userId, active, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active borrows", description = "Retrieves all currently active borrow records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active borrows retrieved successfully")
    })
    public ResponseEntity<Page<BorrowResponse>> getActiveBorrows(
            @Parameter(hidden = true) Pageable pageable) {
        Page<BorrowResponse> response = borrowService.getActiveBorrows(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue borrows", description = "Retrieves all overdue borrow records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdue borrows retrieved successfully")
    })
    public ResponseEntity<List<BorrowResponse>> getOverdueBorrows() {
        List<BorrowResponse> response = borrowService.getOverdueBorrows();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/return")
    @Operation(summary = "Return a book", description = "Marks a borrowed book as returned")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "400", description = "Book already returned")
    })
    public ResponseEntity<BorrowResponse> returnBook(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Book ID") @RequestParam Long bookId) {
        BorrowResponse response = borrowService.returnBook(userId, bookId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/date_extend")
    @Operation(summary = "Extend due date", description = "Extends the due date of a borrowed book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Due date extended successfully"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "400", description = "Cannot extend due date (already returned, overdue, or max extensions reached)")
    })
    public ResponseEntity<BorrowResponse> extendDueDate(
            @Parameter(description = "User ID") @RequestParam Long userId,
<<<<<<< HEAD
            @Parameter(description = "Book ID") @RequestParam Long bookId) {
        BorrowResponse response = borrowService.extendDueDate(userId, bookId);
=======
            @Parameter(description = "Book ID") @RequestParam Long bookId,
            @Parameter(description = "Extension days") @RequestParam(required = false) Integer extendDays){

        // If extendDays is null, use the default (7 days)
        extendDays = (extendDays != null) ? extendDays : 7;
        BorrowResponse response = borrowService.extendDueDate(userId, bookId, extendDays);
>>>>>>> upstream/main
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/history")
    @Operation(summary = "Get user's borrowing history", description = "Retrieves the complete borrowing history for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrowing history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Page<BorrowResponse>> getUserBorrowingHistory(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(hidden = true) Pageable pageable) {
        Page<BorrowResponse> response = borrowService.getUserBorrowingHistory(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/pending")
    @Operation(summary = "Mark borrow request as pending", description = "Marks a borrow request as pending based on user ID and book ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrow request marked as pending successfully"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request or borrow record not found")
    })
    public ResponseEntity<BorrowResponse> markBorrowAsPending(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Book ID") @RequestParam Long bookId) {
        BorrowResponse response = borrowService.markBorrowAsPending(userId, bookId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reject")
    @Operation(summary = "Reject borrow request", description = "Rejects a borrow request based on user ID and book ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrow request rejected successfully"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request or borrow record not found")
    })
    public ResponseEntity<BorrowResponse> rejectBorrowRequest(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Book ID") @RequestParam Long bookId) {
        BorrowResponse response = borrowService.rejectBorrowRequest(userId, bookId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/accept")
    @Operation(summary = "Accept borrow request", description = "Admin accepts a borrow request based on user ID and book ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrow request accepted successfully"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request or borrow record not found")
    })
    public ResponseEntity<BorrowResponse> acceptBorrowRequest(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Book ID") @RequestParam Long bookId) {
        BorrowResponse response = borrowService.acceptBorrowRequest(userId, bookId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/activate")
    @Operation(summary = "Activate borrow request", description = "Activates an accepted borrow request based on user ID and book ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrow request activated successfully"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request or borrow record not found")
    })
    public ResponseEntity<BorrowResponse> activateBorrowRequest(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Book ID") @RequestParam Long bookId) {
        BorrowResponse response = borrowService.activateBorrowRequest(userId, bookId);
        return ResponseEntity.ok(response);
    }
}

