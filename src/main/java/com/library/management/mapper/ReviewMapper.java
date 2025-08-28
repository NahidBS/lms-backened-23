package com.library.management.mapper;

import com.library.management.dto.response.ReviewResponse;
import com.library.management.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewMapper {
    
    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    
    public ReviewResponse toResponse(Review review) {
        if (review == null) {
            return null;
        }
        
        // For now, assume all reviews can be edited/deleted by their authors
        // In a real system, this would check current user context
        Boolean canBeEdited = true;
        Boolean canBeDeleted = true;
        
        return ReviewResponse.builder()
                .id(review.getId())
                .user(userMapper.toResponse(review.getUser()))
                .book(bookMapper.toResponse(review.getBook()))
                .rating(review.getRating())
                .comment(review.getComment())
                .canBeEdited(canBeEdited)
                .canBeDeleted(canBeDeleted)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
    
    public List<ReviewResponse> toResponseList(List<Review> reviews) {
        if (reviews == null) {
            return null;
        }
        
        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

