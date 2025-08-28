package com.library.management.service;

import com.library.management.dto.request.BookingCreateRequest;
import com.library.management.dto.response.BookingResponse;
import com.library.management.entity.Book;
import com.library.management.entity.Booking;
import com.library.management.entity.User;
import com.library.management.exception.BusinessLogicException;
import com.library.management.exception.ResourceAlreadyExistsException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.mapper.BookingMapper;
import com.library.management.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final BookService bookService;
    
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {
        // Verify user and book exist
        User user = userService.getUserEntityById(request.getUserId());
        Book book = bookService.getBookEntityById(request.getBookId());
        
        // Check if book is currently available
        if (book.getAvailableCopies() > 0) {
            throw new BusinessLogicException("Book is currently available for borrowing. No need to book.");
        }
        
        // Check if user already has a pending booking for this book
        if (bookingRepository.findPendingBookingByUserAndBook(request.getUserId(), request.getBookId()).isPresent()) {
            throw new ResourceAlreadyExistsException("Booking", "user and book", request.getUserId() + " and " + request.getBookId());
        }
        
        // Calculate expected available date if not provided
        LocalDate expectedDate = request.getExpectedAvailableDate();
        if (expectedDate == null) {
            expectedDate = calculateExpectedAvailableDate(book);
        }
        
        Booking booking = Booking.builder()
                .user(user)
                .book(book)
                .bookingDate(LocalDate.now())
                .expectedAvailableDate(expectedDate)
                .status(Booking.BookingStatus.PENDING)
                .build();
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(savedBooking);
    }
    
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        return bookingMapper.toResponse(booking);
    }
    
    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookings(Long userId, Long bookId, String status, Pageable pageable) {
        Page<Booking> bookings;
        
        if (userId != null && bookId != null && status != null) {
            // Filter by user, book, and status - would need custom query
            bookings = bookingRepository.findAll(pageable);
        } else if (userId != null && status != null) {
            bookings = bookingRepository.findAll(pageable);
        } else if (bookId != null && status != null) {
            bookings = bookingRepository.findAll(pageable);
        } else if (userId != null) {
            bookings = bookingRepository.findAll(pageable);
        } else if (bookId != null) {
            bookings = bookingRepository.findAll(pageable);
        } else if (status != null) {
            Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
            bookings = bookingRepository.findAll(pageable);
        } else {
            bookings = bookingRepository.findAll(pageable);
        }
        
        return bookings.map(bookingMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId, String status) {
        // Verify user exists
        userService.getUserEntityById(userId);
        
        List<Booking> bookings;
        if (status != null) {
            Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
            bookings = bookingRepository.findByUserIdAndStatus(userId, bookingStatus);
        } else {
            bookings = bookingRepository.findByUserId(userId);
        }
        
        return bookingMapper.toResponseList(bookings);
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookBookings(Long bookId, String status) {
        // Verify book exists
        bookService.getBookEntityById(bookId);
        
        List<Booking> bookings;
        if (status != null) {
            Booking.BookingStatus bookingStatus = Booking.BookingStatus.valueOf(status.toUpperCase());
            bookings = bookingRepository.findByBookIdAndStatus(bookId, bookingStatus);
        } else {
            bookings = bookingRepository.findByBookId(bookId);
        }
        
        return bookingMapper.toResponseList(bookings);
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getPendingBookings() {
        List<Booking> bookings = bookingRepository.findByStatus(Booking.BookingStatus.PENDING);
        return bookingMapper.toResponseList(bookings);
    }
    
    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BusinessLogicException("Only pending bookings can be cancelled");
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponse(updatedBooking);
    }
    
    @Transactional
    public void fulfillBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
        
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BusinessLogicException("Only pending bookings can be fulfilled");
        }
        
        booking.setStatus(Booking.BookingStatus.FULFILLED);
        bookingRepository.save(booking);
    }
    
    @Transactional
    public void expireOldBookings() {
        List<Booking> expiredBookings = bookingRepository.findExpiredBookings(LocalDate.now());
        for (Booking booking : expiredBookings) {
            booking.setStatus(Booking.BookingStatus.EXPIRED);
        }
        bookingRepository.saveAll(expiredBookings);
    }
    
    private LocalDate calculateExpectedAvailableDate(Book book) {
        // Simple calculation: add 14 days (default borrow period) from today
        // In a real system, this would be more sophisticated based on current borrows
        return LocalDate.now().plusDays(14);
    }
}

