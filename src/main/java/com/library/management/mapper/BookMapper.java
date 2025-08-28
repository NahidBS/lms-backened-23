package com.library.management.mapper;

import com.library.management.dto.request.BookCreateRequest;
import com.library.management.dto.request.BookUpdateRequest;
import com.library.management.dto.response.BookResponse;
import com.library.management.entity.Book;
import com.library.management.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    public Book toEntity(BookCreateRequest request, Category category) {
        if (request == null) {
            return null;
        }
        
        return Book.builder()
                .name(request.getName())
                .shortDetails(request.getShortDetails())
                .author(request.getAuthor())
                .about(request.getAbout())
                .category(category)
                .format(request.getFormat())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getAvailableCopies() != null ? 
                    request.getAvailableCopies() : request.getTotalCopies())
                .isbn(request.getIsbn())
                .publicationYear(request.getPublicationYear())
                .build();
    }
    
    public BookResponse toResponse(Book book) {
        if (book == null) {
            return null;
        }
        
        return BookResponse.builder()
                .id(book.getId())
                .name(book.getName())
                .shortDetails(book.getShortDetails())
                .author(book.getAuthor())
                .about(book.getAbout())
                .category(categoryMapper.toResponseWithoutBooks(book.getCategory()))
                .format(book.getFormat())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .averageRating(calculateAverageRating(book))
                .reviewCount(book.getReviews() != null ? book.getReviews().size() : 0)
                .isAvailable(book.getAvailableCopies() != null && book.getAvailableCopies() > 0)
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .bookCoverUrl(book.getBookCoverUrl())
                .pdfFileUrl(book.getPdfFileUrl())
                .audioFileUrl(book.getAudioFileUrl())
                .build();
    }
    
    public BookResponse toResponseWithoutCategory(Book book) {
        if (book == null) {
            return null;
        }
        
        return BookResponse.builder()
                .id(book.getId())
                .name(book.getName())
                .shortDetails(book.getShortDetails())
                .author(book.getAuthor())
                .about(book.getAbout())
                .category(null) // Will be set separately if needed
                .format(book.getFormat())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .averageRating(calculateAverageRating(book))
                .reviewCount(book.getReviews() != null ? book.getReviews().size() : 0)
                .isAvailable(book.getAvailableCopies() != null && book.getAvailableCopies() > 0)
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .bookCoverUrl(book.getBookCoverUrl())
                .pdfFileUrl(book.getPdfFileUrl())
                .audioFileUrl(book.getAudioFileUrl())
                .build();
    }
    
    public void updateEntity(Book book, BookUpdateRequest request, Category category) {
        if (book == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            book.setName(request.getName());
        }
        if (request.getShortDetails() != null) {
            book.setShortDetails(request.getShortDetails());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getAbout() != null) {
            book.setAbout(request.getAbout());
        }
        if (category != null) {
            book.setCategory(category);
        }
        if (request.getFormat() != null) {
            book.setFormat(request.getFormat());
        }
        if (request.getTotalCopies() != null) {
            book.setTotalCopies(request.getTotalCopies());
        }
        if (request.getAvailableCopies() != null) {
            book.setAvailableCopies(request.getAvailableCopies());
        }
        if (request.getIsbn() != null) {
            book.setIsbn(request.getIsbn());
        }
        if (request.getPublicationYear() != null) {
            book.setPublicationYear(request.getPublicationYear());
        }
    }
    
    public List<BookResponse> toResponseList(List<Book> books) {
        if (books == null) {
            return null;
        }
        
        return books.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    private Double calculateAverageRating(Book book) {
        if (book.getReviews() == null || book.getReviews().isEmpty()) {
            return null;
        }
        
        return book.getReviews().stream()
                .mapToInt(review -> review.getRating())
                .average()
                .orElse(0.0);
    }
}

