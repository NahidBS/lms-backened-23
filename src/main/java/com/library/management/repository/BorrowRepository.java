package com.library.management.repository;

import com.library.management.entity.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    long countByStatus(Borrow.BorrowStatus status);
    
    List<Borrow> findByUserId(Long userId);
    
    Page<Borrow> findByUserId(Long userId, Pageable pageable);
    
    Page<Borrow> findByUserIdAndBookId(Long userId, Long bookId, Pageable pageable);
    
    Page<Borrow> findByUserIdAndReturnDateIsNull(Long userId, Pageable pageable);
    
    Page<Borrow> findByBookId(Long bookId, Pageable pageable);
    
    Page<Borrow> findByReturnDateIsNull(Pageable pageable);
    
    Page<Borrow> findByUserIdOrderByBorrowDateDesc(Long userId, Pageable pageable);
    
    List<Borrow> findByBookId(Long bookId);
    
    List<Borrow> findByStatus(Borrow.BorrowStatus status);
    
    @Query("SELECT b FROM Borrow b WHERE b.user.id = :userId AND b.status = :status")
    List<Borrow> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Borrow.BorrowStatus status);
    
    @Query("SELECT b FROM Borrow b WHERE b.book.id = :bookId AND b.status = :status")
    List<Borrow> findByBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") Borrow.BorrowStatus status);
    
    @Query("SELECT b FROM Borrow b WHERE b.returnDate IS NULL AND b.dueDate < :date")
    List<Borrow> findOverdueBorrows(@Param("date") LocalDate date);
    
    @Query("SELECT b FROM Borrow b WHERE b.returnDate IS NULL AND b.dueDate < :date")
    Page<Borrow> findOverdueBorrows(@Param("date") LocalDate date, Pageable pageable);
    
    @Query("SELECT b FROM Borrow b WHERE b.user.id = :userId AND b.book.id = :bookId AND b.returnDate IS NULL")
    Optional<Borrow> findActiveBorrowByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
    
    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.user.id = :userId AND b.returnDate IS NULL")
    long countActiveBorrowsByUserId(@Param("userId") Long userId);
}

