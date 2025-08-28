package com.library.management.mapper;

import com.library.management.dto.response.BookingResponse;
import com.library.management.entity.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    
    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    
    public BookingResponse toResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        
        Long daysUntilAvailable = null;
        if (booking.getExpectedAvailableDate() != null) {
            daysUntilAvailable = ChronoUnit.DAYS.between(LocalDate.now(), booking.getExpectedAvailableDate());
            if (daysUntilAvailable < 0) {
                daysUntilAvailable = 0L;
            }
        }
        
        Boolean canBeCancelled = booking.getStatus() == Booking.BookingStatus.PENDING;
        
        return BookingResponse.builder()
                .id(booking.getId())
                .user(userMapper.toResponse(booking.getUser()))
                .book(bookMapper.toResponse(booking.getBook()))
                .bookingDate(booking.getBookingDate())
                .expectedAvailableDate(booking.getExpectedAvailableDate())
                .status(booking.getStatus())
                .daysUntilAvailable(daysUntilAvailable)
                .canBeCancelled(canBeCancelled)
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
    
    public List<BookingResponse> toResponseList(List<Booking> bookings) {
        if (bookings == null) {
            return null;
        }
        
        return bookings.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

