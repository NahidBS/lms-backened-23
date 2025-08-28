package com.library.management.service;

import com.library.management.controller.ReviewController;
import com.library.management.dto.request.ReviewCreateRequest;
import com.library.management.dto.request.ReviewUpdateRequest;
import com.library.management.dto.response.ReviewResponse;
import com.library.management.entity.Book;
import com.library.management.entity.Review;
import com.library.management.entity.User;
import com.library.management.exception.BusinessLogicException;
import com.library.management.exception.ResourceAlreadyExistsException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.mapper.ReviewMapper;
import com.library.management.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final BookService bookService;
    
    @Transactional
    public ReviewResponse createReview(Long bookId, ReviewCreateRequest request) {
        // Verify user and book exist
        User user = userService.getUserEntityById(request.getUserId());
        Book book = bookService.getBookEntityById(bookId);
        
        // Check if user already reviewed this book
        if (reviewRepository.findByUserIdAndBookId(request.getUserId(), bookId).isPresent()) {
            throw new ResourceAlreadyExistsException("Review", "user and book", request.getUserId() + " and " + bookId);
        }
        
        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toResponse(savedReview);
    }
    
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        return reviewMapper.toResponse(review);
    }
    
    @Transactional(readOnly = true)
    public List<ReviewResponse> getBookReviews(Long bookId) {
        // Verify book exists
        bookService.getBookEntityById(bookId);
        
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        return reviewMapper.toResponseList(reviews);
    }
    
    @Transactional(readOnly = true)
    public List<ReviewResponse> getUserReviews(Long userId) {
        // Verify user exists
        userService.getUserEntityById(userId);
        
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviewMapper.toResponseList(reviews);
    }
    
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        
        // Check if the user is authorized to update this review
        if (!review.getUser().getId().equals(request.getUserId())) {
            throw new BusinessLogicException("User is not authorized to update this review");
        }
        
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toResponse(updatedReview);
    }
    
    @Transactional
    public void deleteReview(Long id, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        
        // Check if the user is authorized to delete this review
        if (!review.getUser().getId().equals(userId)) {
            throw new BusinessLogicException("User is not authorized to delete this review");
        }
        
        reviewRepository.delete(review);
    }
    
    @Transactional(readOnly = true)
    public ReviewController.ReviewStatsResponse getBookReviewStats(Long bookId) {
        // Verify book exists
        bookService.getBookEntityById(bookId);
        
        Double averageRating = reviewRepository.getAverageRatingByBookId(bookId);
        Long totalReviews = reviewRepository.countByBookId(bookId);
        
        return new ReviewController.ReviewStatsResponse(averageRating, totalReviews);
    }
}

