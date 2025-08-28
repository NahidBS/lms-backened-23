package com.library.management.repository;

import com.library.management.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    Optional<Book> findByIsbn(String isbn);
    
    List<Book> findByNameContainingIgnoreCase(String name);
    
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    Page<Book> findByCategoryId(Long categoryId, Pageable pageable);
    
    Page<Book> findByCategoryIdAndAvailableCopiesGreaterThan(Long categoryId, int availableCopies, Pageable pageable);
    
    Page<Book> findByAvailableCopiesGreaterThan(int availableCopies, Pageable pageable);
    
    Page<Book> findByNameContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
            String name, String author, String isbn, Pageable pageable);
    
    @Query("SELECT b FROM Book b ORDER BY b.id DESC")
    Page<Book> findTrendingBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    List<Book> findAvailableBooks();
    
    @Query("SELECT b FROM Book b ORDER BY b.createdAt DESC")
    List<Book> findNewBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b ORDER BY b.id DESC")
    List<Book> findPopularBooks(Pageable pageable);
    
    Page<Book> findAll(Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double getAverageRating(@Param("bookId") Long bookId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId")
    long getReviewCount(@Param("bookId") Long bookId);
}

