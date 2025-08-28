package com.library.management.controller;

import com.library.management.dto.request.BookingCreateRequest;
import com.library.management.dto.response.BookingResponse;
import com.library.management.service.BookingService;
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
@RequestMapping("/api/booking")
@RequiredArgsConstructor
@Tag(name = "Booking Management", description = "APIs for managing book bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    @Operation(summary = "Create a new booking", description = "Creates a new book booking request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "409", description = "Booking already exists or book is available")
    })
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingCreateRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all bookings", description = "Retrieves all booking records with pagination and optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking records retrieved successfully")
    })
    public ResponseEntity<Page<BookingResponse>> getAllBookings(
            @Parameter(description = "User ID filter") @RequestParam(required = false) Long userId,
            @Parameter(description = "Book ID filter") @RequestParam(required = false) Long bookId,
            @Parameter(description = "Status filter") @RequestParam(required = false) String status,
            @Parameter(hidden = true) Pageable pageable) {
        Page<BookingResponse> response = bookingService.getAllBookings(userId, bookId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/retrieve/{id}")
    @Operation(summary = "Retrieve booking details", description = "Retrieves detailed information about a booking record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Booking record not found")
    })
    public ResponseEntity<BookingResponse> retrieveBooking(
            @Parameter(description = "Booking ID") @PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/delete/{id}")
    @Operation(summary = "Cancel a booking", description = "Cancels a pending booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Booking record not found"),
            @ApiResponse(responseCode = "400", description = "Booking cannot be cancelled")
    })
    public ResponseEntity<BookingResponse> cancelBooking(
            @Parameter(description = "Booking ID") @PathVariable Long id) {
        BookingResponse response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's bookings", description = "Retrieves all booking records for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User bookings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<BookingResponse>> getUserBookings(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Status filter") @RequestParam(required = false) String status) {
        List<BookingResponse> response = bookingService.getUserBookings(userId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get book's bookings", description = "Retrieves all booking records for a specific book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book bookings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<List<BookingResponse>> getBookBookings(
            @Parameter(description = "Book ID") @PathVariable Long bookId,
            @Parameter(description = "Status filter") @RequestParam(required = false) String status) {
        List<BookingResponse> response = bookingService.getBookBookings(bookId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending bookings", description = "Retrieves all pending booking records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending bookings retrieved successfully")
    })
    public ResponseEntity<List<BookingResponse>> getPendingBookings() {
        List<BookingResponse> response = bookingService.getPendingBookings();
        return ResponseEntity.ok(response);
    }
}

