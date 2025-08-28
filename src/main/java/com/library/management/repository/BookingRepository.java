package com.library.management.repository;

import com.library.management.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByBookId(Long bookId);
    
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
    List<Booking> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.book.id = :bookId AND b.status = :status")
    List<Booking> findByBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.expectedAvailableDate < :date")
    List<Booking> findExpiredBookings(@Param("date") LocalDate date);
    
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.book.id = :bookId AND b.status = 'PENDING'")
    Optional<Booking> findPendingBookingByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}

