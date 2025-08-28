package com.library.management.controller;

import com.library.management.dto.request.BookCreateRequest;
import com.library.management.dto.request.BookUpdateRequest;
import com.library.management.dto.response.BookResponse;
import com.library.management.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
@Tag(name = "Book Management", description = "APIs for managing books in the library")
public class BookController {

    private final BookService bookService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * Base directory on disk where uploaded files should be stored. Defaults to
     * the relative "uploads" folder if the application property
     * {@code file.upload-dir} is not set. This allows for storing files in an
     * absolute location when configured (e.g. "/var/lib/myapp/uploads").
     */
    @Value("${file.upload-dir:uploads}")
    private String uploadBaseDir;

    @PostMapping("/create/with-links")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new book with optional file links",
            description = "Adds a new book with optional cover image, PDF, and audio file URLs (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Book with ISBN already exists")
    })
    public ResponseEntity<BookResponse> createBookWithLinks(
            @Valid @RequestBody BookCreateRequest request) {
        
        // Set available copies if not provided
        if (request.getAvailableCopies() == null) {
            request.setAvailableCopies(request.getTotalCopies());
        }

        // Process the request
        BookResponse response = bookService.createBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Create a new book using a JSON payload. This endpoint is retained for
     * backwards compatibility and accepts a standard JSON request body. When
     * uploading files (e.g. a book cover image) please use the multipart
     * variant of this endpoint defined below.
     *
     * @param request the book details
     * @return the created book response
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new book (JSON)", description = "Adds a new book to the library using a JSON body (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Book with ISBN already exists")
    })
    public ResponseEntity<BookResponse> createBookJson(
            @Valid @RequestBody BookCreateRequest request) {
        // Default available copies to total copies when omitted to ensure logical consistency
        if (request.getAvailableCopies() == null) {
            request.setAvailableCopies(request.getTotalCopies());
        }
        BookResponse response = bookService.createBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Create a new book with an optional uploaded book cover, PDF and audio file.
     * This endpoint accepts a multipart/form-data request containing a
     * {@code BookCreateRequest} as the {@code book} part and optional file
     * uploads. Files will be stored in the configured uploads directory and the
     * resulting URLs will be added to the request before persisting. If no files
     * are provided the behaviour is identical to the JSON endpoint.
     *
     * @param request   the book details sent in the multipart {@code book} part
     * @param bookCover the image file for the book cover (optional)
     * @param pdfFile   the PDF file for the book (optional)
     * @param audioFile the audio file for the audiobook (optional)
     * @return the created book response
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new book (multipart)", description = "Adds a new book to the library with optional uploaded files (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Book with ISBN already exists")
    })
    public ResponseEntity<BookResponse> createBookWithFiles(
            @RequestPart("book") String bookJson,
            @RequestPart(value = "bookCover", required = false) org.springframework.web.multipart.MultipartFile bookCover,
            @RequestPart(value = "pdfFile", required = false) org.springframework.web.multipart.MultipartFile pdfFile,
            @RequestPart(value = "audioFile", required = false) org.springframework.web.multipart.MultipartFile audioFile) {
        // Parse the JSON part into a BookCreateRequest. This allows the client to omit
        // the content-type header on the part while still sending valid JSON.
        BookCreateRequest request;
        try {
            request = objectMapper.readValue(bookJson, BookCreateRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON for book part", e);
        }

        // Validate the parsed request manually since @Valid is not applied to String parts.
        Set<ConstraintViolation<BookCreateRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Ensure availableCopies defaults to totalCopies when omitted
        if (request.getAvailableCopies() == null) {
            request.setAvailableCopies(request.getTotalCopies());
        }

        // Handle cover image upload
        if (bookCover != null && !bookCover.isEmpty()) {
            if (!com.library.management.util.FileValidationUtil.isValidImageFile(bookCover)) {
                throw new com.library.management.exception.BusinessLogicException("Invalid image file. Only JPEG and PNG formats up to 10MB are allowed.");
            }
            try {
                // Determine the upload directory and ensure it exists
                java.nio.file.Path uploadDir = java.nio.file.Paths.get(uploadBaseDir, "covers");
                java.nio.file.Files.createDirectories(uploadDir);

                // Build a unique filename preserving the original extension
                String extension = com.library.management.util.FileValidationUtil.getFileExtension(bookCover.getOriginalFilename());
                String filename = java.util.UUID.randomUUID().toString() + extension;
                java.nio.file.Path destination = uploadDir.resolve(filename);

                // Copy the file to the target location, replacing existing file if present
                try (java.io.InputStream inputStream = bookCover.getInputStream()) {
                    java.nio.file.Files.copy(inputStream, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                // Build a full URL including the server context path so clients
                // receive an absolute URL (e.g. http://localhost:8080/files/covers/<file>). The
                // ServletUriComponentsBuilder uses the current request context to
                // determine the host and port.
                String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/files/covers/")
                        .path(filename)
                        .toUriString();
                request.setBookCoverUrl(url);
            } catch (java.io.IOException e) {
                throw new com.library.management.exception.BusinessLogicException("Failed to store book cover file", e);
            }
        }

        // Handle PDF upload
        if (pdfFile != null && !pdfFile.isEmpty()) {
            if (!com.library.management.util.FileValidationUtil.isValidPdfFile(pdfFile)) {
                throw new com.library.management.exception.BusinessLogicException("Invalid PDF file. Only PDF files up to 10MB are allowed.");
            }
            try {
                java.nio.file.Path uploadDir = java.nio.file.Paths.get(uploadBaseDir, "pdfs");
                java.nio.file.Files.createDirectories(uploadDir);
                String extension = com.library.management.util.FileValidationUtil.getFileExtension(pdfFile.getOriginalFilename());
                String filename = java.util.UUID.randomUUID().toString() + extension;
                java.nio.file.Path destination = uploadDir.resolve(filename);
                try (java.io.InputStream inputStream = pdfFile.getInputStream()) {
                    java.nio.file.Files.copy(inputStream, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/files/pdfs/")
                        .path(filename)
                        .toUriString();
                request.setPdfFileUrl(url);
            } catch (java.io.IOException e) {
                throw new com.library.management.exception.BusinessLogicException("Failed to store PDF file", e);
            }
        }

        // Handle audio upload
        if (audioFile != null && !audioFile.isEmpty()) {
            // Validate file size manually since audio MIME types can vary
            if (audioFile.getSize() > (10 * 1024 * 1024)) {
                throw new com.library.management.exception.BusinessLogicException("Audio file must not exceed 10MB");
            }
            try {
                java.nio.file.Path uploadDir = java.nio.file.Paths.get(uploadBaseDir, "audio");
                java.nio.file.Files.createDirectories(uploadDir);
                String extension = com.library.management.util.FileValidationUtil.getFileExtension(audioFile.getOriginalFilename());
                String filename = java.util.UUID.randomUUID().toString() + extension;
                java.nio.file.Path destination = uploadDir.resolve(filename);
                try (java.io.InputStream inputStream = audioFile.getInputStream()) {
                    java.nio.file.Files.copy(inputStream, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/files/audio/")
                        .path(filename)
                        .toUriString();
                request.setAudioFileUrl(url);
            } catch (java.io.IOException e) {
                throw new com.library.management.exception.BusinessLogicException("Failed to store audio file", e);
            }
        }

        // Delegate creation to service layer
        BookResponse response = bookService.createBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/is_available")
    @Operation(summary = "Check book availability", description = "Checks if a book is available for borrowing (User/Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book availability checked successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Boolean> isBookAvailable(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        Boolean isAvailable = bookService.isBookAvailable(id);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/retrieve/{id}")
    @Operation(summary = "Retrieve book details", description = "Retrieves detailed information about a book (User/Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookResponse> retrieveBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        BookResponse response = bookService.getBookById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    @Operation(summary = "Get all books", description = "Retrieves all books with pagination and optional filtering (User/Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    })
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @Parameter(description = "Category ID filter") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Availability filter") @RequestParam(required = false) Boolean available,
            @Parameter(hidden = true) Pageable pageable) {
        Page<BookResponse> response = bookService.getAllBooks(categoryId, available, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search books by title, author, or ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<Page<BookResponse>> searchBooks(
            @Parameter(description = "Search query (title, author, or ISBN)") @RequestParam String query,
            @Parameter(hidden = true) Pageable pageable) {
        Page<BookResponse> response = bookService.searchBooks(query, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get books by category", description = "Retrieves all books in a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Page<BookResponse>> getBooksByCategory(
            @Parameter(description = "Category ID") @PathVariable Long categoryId,
            @Parameter(hidden = true) Pageable pageable) {
        Page<BookResponse> response = bookService.getBooksByCategory(categoryId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    @Operation(summary = "Get available books", description = "Retrieves all books that are currently available for borrowing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available books retrieved successfully")
    })
    public ResponseEntity<Page<BookResponse>> getAvailableBooks(
            @Parameter(hidden = true) Pageable pageable) {
        Page<BookResponse> response = bookService.getAvailableBooks(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular-books")
    @Operation(summary = "Get popular books", description = "Retrieves the most borrowed books (User)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Popular books retrieved successfully")
    })
    public ResponseEntity<List<BookResponse>> getPopularBooks(
            @Parameter(description = "Number of books to return") @RequestParam(defaultValue = "10") int limit) {
        List<BookResponse> response = bookService.getPopularBooks(limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommended-books")
    @Operation(summary = "Get recommended books", description = "Retrieves recommended books based on user preferences (User)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recommended books retrieved successfully")
    })
    public ResponseEntity<List<BookResponse>> getRecommendedBooks(
            @Parameter(description = "Number of books to return") @RequestParam(defaultValue = "10") int limit) {
        List<BookResponse> response = bookService.getRecommendedBooks(limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/new-collection")
    @Operation(summary = "Get new collection", description = "Retrieves newly added books (User)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New collection retrieved successfully")
    })
    public ResponseEntity<List<BookResponse>> getNewCollection(
            @Parameter(description = "Number of books to return") @RequestParam(defaultValue = "10") int limit) {
        List<BookResponse> response = bookService.getNewBooks(limit);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "Update book", description = "Updates an existing book (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Book or category not found"),
            @ApiResponse(responseCode = "409", description = "ISBN already exists for another book")
    })
    public ResponseEntity<BookResponse> updateBook(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request) {
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete book", description = "Deletes a book if it has no active borrows (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "409", description = "Book has active borrows")
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "Book ID") @PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Update book availability", description = "Updates the available copies of a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book availability updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid availability count"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookResponse> updateBookAvailability(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "New available copies count") @RequestParam int availableCopies) {
        BookResponse response = bookService.updateBookAvailability(id, availableCopies);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/category")
//    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update book category", description = "Updates only the category of a book (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book category updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book or category not found")
    })
    public ResponseEntity<BookResponse> updateBookCategory(
            @Parameter(description = "Book ID") @PathVariable Long id,
            @Parameter(description = "New Category ID") @RequestParam Long categoryId) {
        BookResponse response = bookService.updateBookCategory(id, categoryId);
        return ResponseEntity.ok(response);
    }



}

