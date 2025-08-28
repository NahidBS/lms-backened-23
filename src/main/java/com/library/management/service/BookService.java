package com.library.management.service;

import com.library.management.dto.request.BookCreateRequest;
import com.library.management.dto.request.BookUpdateRequest;
import com.library.management.dto.response.BookResponse;
import com.library.management.entity.Book;
import com.library.management.entity.Category;
import com.library.management.exception.BusinessLogicException;
import com.library.management.exception.ResourceAlreadyExistsException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.mapper.BookMapper;
import com.library.management.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CategoryService categoryService;
    private final NotificationService notificationService;
    private final RecommendationService recommendationService;

    @Transactional
    public BookResponse createBook(BookCreateRequest request) {
        if (request.getIsbn() != null && bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new ResourceAlreadyExistsException("Book", "ISBN", request.getIsbn());
        }

        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        // Convert DTO to entity
        Book book = bookMapper.toEntity(request, category);

        // Set URL fields from request
        book.setBookCoverUrl(request.getBookCoverUrl());
        book.setPdfFileUrl(request.getPdfFileUrl());
        book.setAudioFileUrl(request.getAudioFileUrl());

        // Validate rules and save
        validateBookBusinessRules(book);

        Book savedBook = bookRepository.save(book);
        
        // Create response DTO from entity
        BookResponse response = bookMapper.toResponse(savedBook);

        return response;
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = getBookEntityById(id);
        return bookMapper.toResponse(book);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(Long categoryId, Boolean available, Pageable pageable) {
        Page<Book> books;
        
        if (categoryId != null && available != null && available) {
            books = bookRepository.findByCategoryIdAndAvailableCopiesGreaterThan(categoryId, 0, pageable);
        } else if (categoryId != null) {
            books = bookRepository.findByCategoryId(categoryId, pageable);
        } else if (available != null && available) {
            books = bookRepository.findByAvailableCopiesGreaterThan(0, pageable);
        } else {
            books = bookRepository.findAll(pageable);
        }
        
        return books.map(bookMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(String query, Pageable pageable) {
        Page<Book> books = bookRepository.findByNameContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                query, query, query, pageable);
        return books.map(bookMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getBooksByCategory(Long categoryId, Pageable pageable) {
        // Verify category exists
        categoryService.getCategoryEntityById(categoryId);
        
        Page<Book> books = bookRepository.findByCategoryId(categoryId, pageable);
        return books.map(bookMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getAvailableBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findByAvailableCopiesGreaterThan(0, pageable);
        return books.map(bookMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getPopularBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Book> books = bookRepository.findPopularBooks(pageable);
        return books.stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getRecommendedBooks(int limit) {
        // Simple implementation - return trending books
        Pageable pageable = PageRequest.of(0, limit);
        Page<Book> books = bookRepository.findTrendingBooks(pageable);
        return books.stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getNewBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Book> books = bookRepository.findNewBooks(pageable);
        return books.stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Transactional
    public BookResponse updateBook(Long id, BookUpdateRequest request) {
        Book book = getBookEntityById(id);

        // Check if ISBN is being changed and if it already exists
        if (request.getIsbn() != null && !request.getIsbn().equals(book.getIsbn())) {
            if (bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
                throw new ResourceAlreadyExistsException("Book", "ISBN", request.getIsbn());
            }
        }

        // Get category if provided
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryService.getCategoryEntityById(request.getCategoryId());
        }

        // Update book fields
        bookMapper.updateEntity(book, request, category);

        // Validate business rules
        validateBookBusinessRules(book);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = getBookEntityById(id);
        
        // Simple check - just delete the book
        bookRepository.delete(book);
    }

    @Transactional
    public BookResponse updateBookAvailability(Long id, int availableCopies) {
        Book book = getBookEntityById(id);
        
        if (availableCopies < 0 || availableCopies > book.getTotalCopies()) {
            throw new BusinessLogicException("Available copies must be between 0 and " + book.getTotalCopies());
        }

        book.setAvailableCopies(availableCopies);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    @Transactional(readOnly = true)
    public Boolean isBookAvailable(Long id) {
        Book book = getBookEntityById(id);
        return book.getAvailableCopies() > 0;
    }

    // Helper methods
    public Book getBookEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }

    @Transactional
    public void decrementAvailableCopies(Long bookId) {
        Book book = getBookEntityById(bookId);
        
        if (book.getAvailableCopies() <= 0) {
            throw new BusinessLogicException("No available copies for this book");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        
        bookRepository.save(book);
    }

    @Transactional
    public void incrementAvailableCopies(Long bookId) {
        Book book = getBookEntityById(bookId);
        
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new BusinessLogicException("Available copies cannot exceed total copies");
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        
        bookRepository.save(book);
    }

    private void validateBookBusinessRules(Book book) {
        // Validate available copies
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new BusinessLogicException("Available copies cannot exceed total copies");
        }
    }

    @Transactional
    public BookResponse updateBookCategory(Long bookId, Long categoryId) {
        Book book = getBookEntityById(bookId);
        Category category = categoryService.getCategoryEntityById(categoryId);

        book.setCategory(category);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }
}

