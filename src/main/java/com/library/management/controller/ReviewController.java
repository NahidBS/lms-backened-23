package com.library.management.controller;

import com.library.management.dto.request.ReviewCreateRequest;
import com.library.management.dto.request.ReviewUpdateRequest;
import com.library.management.dto.response.ReviewResponse;
import com.library.management.service.ReviewService;
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

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@Tag(name = "Review Management", description = "APIs for managing book reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/book/{bookId}/create")
    @Operation(summary = "Create a new review", description = "Creates a new book review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
            @ApiResponse(responseCode = "404", description = "User or book not found"),
            @ApiResponse(responseCode = "409", description = "Review already exists for this user and book")
    })
    public ResponseEntity<ReviewResponse> createReview(
            @Parameter(description = "Book ID")
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewCreateRequest request) {
        ReviewResponse response = reviewService.createReview(bookId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/list/book/{bookId}")
    @Operation(summary = "Get all reviews for a book", description = "Retrieves all reviews for a specific book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book reviews retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<List<ReviewResponse>> getBookReviews(
            @Parameter(description = "Book ID") @PathVariable Long bookId) {
        List<ReviewResponse> response = reviewService.getBookReviews(bookId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/retrieve/{id}")
    @Operation(summary = "Retrieve review details", description = "Retrieves detailed information about a review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<ReviewResponse> retrieveReview(
            @Parameter(description = "Review ID") @PathVariable Long id) {
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "Update review", description = "Updates an existing review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update this review")
    })
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "Review ID") @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequest request) {
        ReviewResponse response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete review", description = "Deletes a review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this review")
    })
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Review ID") @PathVariable Long id,
            @Parameter(description = "User ID for authorization") @RequestParam Long userId) {
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's reviews", description = "Retrieves all reviews by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User reviews retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<List<ReviewResponse>> getUserReviews(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<ReviewResponse> response = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/book/{bookId}/stats")
    @Operation(summary = "Get book review statistics", description = "Retrieves review statistics for a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book review statistics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<ReviewStatsResponse> getBookReviewStats(
            @Parameter(description = "Book ID") @PathVariable Long bookId) {
        ReviewStatsResponse response = reviewService.getBookReviewStats(bookId);
        return ResponseEntity.ok(response);
    }

    // Inner class for review statistics response
    public static class ReviewStatsResponse {
        private Double averageRating;
        private Long totalReviews;
        
        public ReviewStatsResponse(Double averageRating, Long totalReviews) {
            this.averageRating = averageRating;
            this.totalReviews = totalReviews;
        }
        
        // Getters and setters
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
        public Long getTotalReviews() { return totalReviews; }
        public void setTotalReviews(Long totalReviews) { this.totalReviews = totalReviews; }
    }
}

