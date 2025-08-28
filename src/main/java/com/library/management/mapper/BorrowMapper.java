package com.library.management.mapper;

import com.library.management.dto.request.BorrowCreateRequest;
import com.library.management.dto.response.BorrowResponse;
import com.library.management.entity.Borrow;
import com.library.management.entity.Book;
import com.library.management.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BorrowMapper {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BookMapper bookMapper;
    
    public Borrow toEntity(BorrowCreateRequest request, User user, Book book) {
        if (request == null) {
            return null;
        }
        
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14); // Default 14 days borrowing period
        
        return Borrow.builder()
                .user(user)
                .book(book)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .status(Borrow.BorrowStatus.ACTIVE)
                .extensionCount(0)
                .build();
    }
    
    public BorrowResponse toResponse(Borrow borrow) {
        if (borrow == null) {
            return null;
        }
        
        return BorrowResponse.builder()
                .id(borrow.getId())
                .user(userMapper.toResponseWithoutBorrowStats(borrow.getUser()))
                .book(bookMapper.toResponseWithoutCategory(borrow.getBook()))
                .borrowDate(borrow.getBorrowDate())
                .dueDate(borrow.getDueDate())
                .returnDate(borrow.getReturnDate())
                .status(borrow.getStatus())
                .extensionCount(borrow.getExtensionCount())
                .isOverdue(isOverdue(borrow))
                .daysOverdue(calculateDaysOverdue(borrow))
                .canBeExtended(canBeExtended(borrow))
                .createdAt(borrow.getCreatedAt())
                .updatedAt(borrow.getUpdatedAt())
                .build();
    }
    
    public BorrowResponse toResponseWithoutDetails(Borrow borrow) {
        if (borrow == null) {
            return null;
        }
        
        return BorrowResponse.builder()
                .id(borrow.getId())
                .user(null) // Will be set separately if needed
                .book(null) // Will be set separately if needed
                .borrowDate(borrow.getBorrowDate())
                .dueDate(borrow.getDueDate())
                .returnDate(borrow.getReturnDate())
                .status(borrow.getStatus())
                .extensionCount(borrow.getExtensionCount())
                .isOverdue(isOverdue(borrow))
                .daysOverdue(calculateDaysOverdue(borrow))
                .canBeExtended(canBeExtended(borrow))
                .createdAt(borrow.getCreatedAt())
                .updatedAt(borrow.getUpdatedAt())
                .build();
    }
    
    public List<BorrowResponse> toResponseList(List<Borrow> borrows) {
        if (borrows == null) {
            return null;
        }
        
        return borrows.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public Boolean isOverdue(Borrow borrow) {
        if (borrow.getStatus() != Borrow.BorrowStatus.ACTIVE) {
            return false;
        }
        
        return LocalDate.now().isAfter(borrow.getDueDate());
    }
    
    public Long calculateDaysOverdue(Borrow borrow) {
        if (!isOverdue(borrow)) {
            return 0L;
        }
        
        return ChronoUnit.DAYS.between(borrow.getDueDate(), LocalDate.now());
    }
    
    public Boolean canBeExtended(Borrow borrow) {
        if (borrow.getStatus() != Borrow.BorrowStatus.ACTIVE) {
            return false;
        }
        
        // Business rule: Can extend maximum 2 times and only if not overdue
        return borrow.getExtensionCount() < 2 && !isOverdue(borrow);
    }
}

