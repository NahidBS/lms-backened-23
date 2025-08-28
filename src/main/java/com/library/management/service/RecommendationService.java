package com.library.management.service;

import com.library.management.dto.response.BookResponse;
import com.library.management.entity.Book;
import com.library.management.entity.Borrow;
import com.library.management.entity.User;
import com.library.management.mapper.BookMapper;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowRepository;
import com.library.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    public List<BookResponse> getPersonalizedRecommendations(Long userId, int limit) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            // If user not found, return popular books
            return getPopularBooks(limit);
        }

        User user = userOpt.get();
        List<Borrow> userBorrows = borrowRepository.findByUserId(userId);

        if (userBorrows.isEmpty()) {
            // New user with no borrowing history - return popular books
            return getPopularBooks(limit);
        }

        // Get categories of books the user has borrowed
        Set<Long> borrowedCategoryIds = userBorrows.stream()
                .map(borrow -> borrow.getBook().getCategory().getId())
                .collect(Collectors.toSet());

        // Get books the user has already borrowed
        Set<Long> borrowedBookIds = userBorrows.stream()
                .map(borrow -> borrow.getBook().getId())
                .collect(Collectors.toSet());

        // Find books in similar categories that the user hasn't borrowed
        List<Book> recommendedBooks = new ArrayList<>();
        
        for (Long categoryId : borrowedCategoryIds) {
            Pageable pageable = PageRequest.of(0, limit * 2); // Get more to filter out borrowed books
            List<Book> categoryBooks = bookRepository.findByCategoryIdAndAvailableCopiesGreaterThan(
                    categoryId, 0, pageable).getContent();
            
            // Filter out books the user has already borrowed
            categoryBooks = categoryBooks.stream()
                    .filter(book -> !borrowedBookIds.contains(book.getId()))
                    .collect(Collectors.toList());
            
            recommendedBooks.addAll(categoryBooks);
        }

        // Remove duplicates and limit results
        recommendedBooks = recommendedBooks.stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());

        // If we don't have enough recommendations, fill with popular books
        if (recommendedBooks.size() < limit) {
            List<Book> popularBooks = bookRepository.findPopularBooks(
                    PageRequest.of(0, limit - recommendedBooks.size()));
            
            // Add popular books that are not already in recommendations
            Set<Long> recommendedBookIds = recommendedBooks.stream()
                    .map(Book::getId)
                    .collect(Collectors.toSet());
            
            popularBooks.stream()
                    .filter(book -> !recommendedBookIds.contains(book.getId()) && 
                                   !borrowedBookIds.contains(book.getId()))
                    .forEach(recommendedBooks::add);
        }

        return bookMapper.toResponseList(recommendedBooks.stream()
                .limit(limit)
                .collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getRecommendationsForNewUser(int limit) {
        return getPopularBooks(limit);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getSimilarBooks(Long bookId, int limit) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        
        if (bookOpt.isEmpty()) {
            return getPopularBooks(limit);
        }

        Book book = bookOpt.get();
        Long categoryId = book.getCategory().getId();

        // Find other books in the same category
        Pageable pageable = PageRequest.of(0, limit + 1); // +1 to exclude the current book
        List<Book> similarBooks = bookRepository.findByCategoryIdAndAvailableCopiesGreaterThan(
                categoryId, 0, pageable).getContent();

        // Remove the current book from recommendations
        similarBooks = similarBooks.stream()
                .filter(b -> !b.getId().equals(bookId))
                .limit(limit)
                .collect(Collectors.toList());

        return bookMapper.toResponseList(similarBooks);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getRecommendationsByAuthor(String author, int limit) {
        List<Book> booksByAuthor = bookRepository.findByAuthorContainingIgnoreCase(author);
        
        // Filter available books and limit results
        List<Book> availableBooks = booksByAuthor.stream()
                .filter(book -> book.getAvailableCopies() > 0)
                .limit(limit)
                .collect(Collectors.toList());

        return bookMapper.toResponseList(availableBooks);
    }

    private List<BookResponse> getPopularBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Book> popularBooks = bookRepository.findPopularBooks(pageable);
        return bookMapper.toResponseList(popularBooks);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getTrendingBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Book> trendingBooks = bookRepository.findTrendingBooks(pageable).getContent();
        return bookMapper.toResponseList(trendingBooks);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getRecommendationsBasedOnRatings(Long userId, int limit) {
        // This is a placeholder for a more sophisticated recommendation system
        // In a real implementation, you would analyze user ratings and preferences
        return getPersonalizedRecommendations(userId, limit);
    }
}

