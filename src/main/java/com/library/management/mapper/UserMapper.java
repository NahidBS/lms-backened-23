package com.library.management.mapper;

import com.library.management.dto.response.UserResponse;
import com.library.management.entity.Borrow;
import com.library.management.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .currentBorrowCount(calculateCurrentBorrowCount(user))
                .totalBorrowCount(user.getBorrows() != null ? user.getBorrows().size() : 0)
                .overdueCount(calculateOverdueCount(user))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    public UserResponse toResponseWithoutBorrowStats(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .currentBorrowCount(0) // Will be set separately if needed
                .totalBorrowCount(0) // Will be set separately if needed
                .overdueCount(0) // Will be set separately if needed
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    public List<UserResponse> toResponseList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    private Integer calculateCurrentBorrowCount(User user) {
        if (user.getBorrows() == null) {
            return 0;
        }
        
        return (int) user.getBorrows().stream()
                .filter(borrow -> borrow.getStatus() == Borrow.BorrowStatus.ACTIVE)
                .count();
    }
    
    private Integer calculateOverdueCount(User user) {
        if (user.getBorrows() == null) {
            return 0;
        }
        
        LocalDate today = LocalDate.now();
        return (int) user.getBorrows().stream()
                .filter(borrow -> borrow.getStatus() == Borrow.BorrowStatus.ACTIVE)
                .filter(borrow -> borrow.getDueDate().isBefore(today))
                .count();
    }
}

