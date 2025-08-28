package com.library.management.service;

import com.library.management.dto.request.BorrowCreateRequest;
import com.library.management.dto.response.BorrowResponse;
import com.library.management.entity.Book;
import com.library.management.entity.Borrow;
import com.library.management.entity.User;
import com.library.management.exception.BusinessLogicException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.mapper.BorrowMapper;
import com.library.management.repository.BorrowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {
    
    private final BorrowRepository borrowRepository;
    private final BorrowMapper borrowMapper;
    private final UserService userService;
    private final BookService bookService;
    
    private static final int MAX_BORROW_LIMIT = 5;
    private static final int BORROW_PERIOD_DAYS = 14;
    private static final int EXTENSION_DAYS = 7;
    private static final int MAX_EXTENSIONS = 2;


    @Transactional(readOnly = true)
    public long countActiveBorrows() {
        return borrowRepository.countByStatus(Borrow.BorrowStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public long countReturnedBorrows() {
        return borrowRepository.countByStatus(Borrow.BorrowStatus.RETURNED);
    }

    @Transactional(readOnly = true)
    public long countOverdueBorrows() {
        return borrowRepository.countByStatus(Borrow.BorrowStatus.OVERDUE);
    }

    @Transactional(readOnly = true)
    public long countTotalBorrows() {
        return borrowRepository.count();
    }

    @Transactional(readOnly = true)
    public long countRequestedBorrows() {
        return borrowRepository.countByStatus(Borrow.BorrowStatus.REQUESTED);
    }

    @Transactional(readOnly = true)
    public long countRejectedBorrows() {
        return borrowRepository.countByStatus(Borrow.BorrowStatus.REJECTED);
    }


    @Transactional
    public BorrowResponse borrowBook(BorrowCreateRequest request) {
        return createBorrow(request);
    }

    @Transactional(readOnly = true)
    public Page<BorrowResponse> getAllBorrows(Long userId, Long bookId, Boolean active, Boolean overdue, Pageable pageable) {
        Page<Borrow> borrows;
        
        if (userId != null && bookId != null) {
            borrows = borrowRepository.findByUserIdAndBookId(userId, bookId, pageable);
        } else if (userId != null) {
            if (active != null && active) {
                borrows = borrowRepository.findByUserIdAndReturnDateIsNull(userId, pageable);
            } else {
                borrows = borrowRepository.findByUserId(userId, pageable);
            }
        } else if (bookId != null) {
            borrows = borrowRepository.findByBookId(bookId, pageable);
        } else if (active != null && active) {
            borrows = borrowRepository.findByReturnDateIsNull(pageable);
        } else if (overdue != null && overdue) {
            borrows = borrowRepository.findOverdueBorrows(LocalDate.now(), pageable);
        } else {
            borrows = borrowRepository.findAll(pageable);
        }
        
        return borrows.map(borrowMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<BorrowResponse> getUserBorrows(Long userId, Boolean active, Pageable pageable) {
        Page<Borrow> borrows;
        if (active != null && active) {
            borrows = borrowRepository.findByUserIdAndReturnDateIsNull(userId, pageable);
        } else {
            borrows = borrowRepository.findByUserId(userId, pageable);
        }
        return borrows.map(borrowMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<BorrowResponse> getActiveBorrows(Pageable pageable) {
        Page<Borrow> borrows = borrowRepository.findByReturnDateIsNull(pageable);
        return borrows.map(borrowMapper::toResponse);
    }
    
    @Transactional
<<<<<<< HEAD
    public BorrowResponse extendDueDate(Long userId, Long bookId) {
        return extendBorrow(userId, bookId);
=======
    public BorrowResponse extendDueDate(Long userId, Long bookId, Integer extendDays) {
        return extendBorrow(userId, bookId, extendDays);
>>>>>>> upstream/main
    }
    
    @Transactional(readOnly = true)
    public Page<BorrowResponse> getUserBorrowingHistory(Long userId, Pageable pageable) {
        Page<Borrow> borrows = borrowRepository.findByUserIdOrderByBorrowDateDesc(userId, pageable);
        return borrows.map(borrowMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public BorrowResponse getBorrowById(Long id) {
        Borrow borrow = borrowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow", "id", id));
        return borrowMapper.toResponse(borrow);
    }
    
    @Transactional(readOnly = true)
    public List<BorrowResponse> getBorrowsByUserId(Long userId) {
        List<Borrow> borrows = borrowRepository.findByUserId(userId);
        return borrowMapper.toResponseList(borrows);
    }
    
    @Transactional(readOnly = true)
    public List<BorrowResponse> getActiveBorrowsByUserId(Long userId) {
        List<Borrow> borrows = borrowRepository.findByUserIdAndStatus(userId, Borrow.BorrowStatus.ACTIVE);
        return borrowMapper.toResponseList(borrows);
    }
    
    @Transactional(readOnly = true)
    public List<BorrowResponse> getOverdueBorrows() {
        List<Borrow> borrows = borrowRepository.findOverdueBorrows(LocalDate.now());
        return borrowMapper.toResponseList(borrows);
    }
    
    @Transactional
    public BorrowResponse createBorrow(BorrowCreateRequest request) {
        User user = userService.getUserEntityById(request.getUserId());
        Book book = bookService.getBookEntityById(request.getBookId());
        
        // Validate business rules
        validateBorrowBusinessRules(user, book);
        
        // Check if user already has this book borrowed
        if (borrowRepository.findActiveBorrowByUserAndBook(user.getId(), book.getId()).isPresent()) {
            throw new BusinessLogicException("User already has this book borrowed");
        }
        
        Borrow borrow = borrowMapper.toEntity(request, user, book);
        borrow.setStatus(Borrow.BorrowStatus.REQUESTED);
        
        // Decrease available copies
        bookService.decrementAvailableCopies(book.getId());
        
        Borrow savedBorrow = borrowRepository.save(borrow);
        return borrowMapper.toResponse(savedBorrow);
    }
    
    @Transactional
    public BorrowResponse returnBook(Long userId, Long bookId) {
        Borrow borrow = borrowRepository.findActiveBorrowByUserAndBook(userId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Active borrow record not found for user ID: " + userId + " and book ID: " + bookId));
        
        if (borrow.getStatus() != Borrow.BorrowStatus.ACTIVE) {
            throw new BusinessLogicException("Book is not currently borrowed");
        }
        
        borrow.setReturnDate(LocalDate.now());
        borrow.setStatus(Borrow.BorrowStatus.RETURNED);
        
        // Increase available copies
        bookService.incrementAvailableCopies(borrow.getBook().getId());
        
        Borrow updatedBorrow = borrowRepository.save(borrow);
        return borrowMapper.toResponse(updatedBorrow);
    }
    
    @Transactional
<<<<<<< HEAD
    public BorrowResponse extendBorrow(Long userId, Long bookId) {
        Borrow borrow = borrowRepository.findActiveBorrowByUserAndBook(userId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Active borrow record not found for user ID: " + userId + " and book ID: " + bookId));
        
        if (borrow.getStatus() != Borrow.BorrowStatus.ACTIVE) {
            throw new BusinessLogicException("Only active borrows can be extended");
        }
        
        if (borrow.getExtensionCount() >= MAX_EXTENSIONS) {
            throw new BusinessLogicException(
                String.format("Maximum number of extensions (%d) reached", MAX_EXTENSIONS));
        }
        
        if (borrowMapper.isOverdue(borrow)) {
            throw new BusinessLogicException("Overdue books cannot be extended");
        }
        
        borrow.setDueDate(borrow.getDueDate().plusDays(EXTENSION_DAYS));
=======
    public BorrowResponse extendBorrow(Long userId, Long bookId, Integer extendDays) {
        // Validate extendDays input
        if (extendDays == null) {
            // If no extendDays provided,
            extendDays = EXTENSION_DAYS;
        } else if (extendDays <= 0) {
            // Handle invalid extension days (negative or zero)
            throw new IllegalArgumentException("Extension days must be greater than zero.");
        }
        // Retrieve the active borrow record for the user and book
        Borrow borrow = borrowRepository.findActiveBorrowByUserAndBook(userId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Active borrow record not found for user ID: " + userId + " and book ID: " + bookId));

        // Check if the borrow status is active
        if (borrow.getStatus() != Borrow.BorrowStatus.ACTIVE) {
            throw new BusinessLogicException("Only active borrows can be extended");
        }

        // Check the maximum number of extensions
        if (borrow.getExtensionCount() >= MAX_EXTENSIONS) {
            throw new BusinessLogicException(
                    String.format("Maximum number of extensions (%d) reached", MAX_EXTENSIONS));
        }

        // Check if the borrow is overdue, as overdue books cannot be extended
        if (borrowMapper.isOverdue(borrow)) {
            throw new BusinessLogicException("Overdue books cannot be extended");
        }

        // Calculate the new due date by adding the extension days
        LocalDate newDueDate = borrow.getDueDate().plusDays(extendDays);

        // Update the borrow record with the new due date and increment the extension count
        borrow.setDueDate(newDueDate);
>>>>>>> upstream/main
        borrow.setExtensionCount(borrow.getExtensionCount() + 1);
        
        Borrow updatedBorrow = borrowRepository.save(borrow);
        return borrowMapper.toResponse(updatedBorrow);
    }
    
    @Transactional
    public void updateOverdueStatus() {
        List<Borrow> overdueBorrows = borrowRepository.findOverdueBorrows(LocalDate.now());
        
        for (Borrow borrow : overdueBorrows) {
            if (borrow.getStatus() == Borrow.BorrowStatus.ACTIVE) {
                borrow.setStatus(Borrow.BorrowStatus.OVERDUE);
                borrowRepository.save(borrow);
            }
        }
    }
    
    private void validateBorrowBusinessRules(User user, Book book) {
        // Check if book is available
        if (book.getAvailableCopies() <= 0) {
            throw new BusinessLogicException("Book is not available for borrowing");
        }
        
        // Check user's borrow limit
        long activeBorrowCount = userService.getActiveBorrowCount(user.getId());
        if (activeBorrowCount >= MAX_BORROW_LIMIT) {
            throw new BusinessLogicException(
                String.format("User has reached maximum borrow limit of %d books", MAX_BORROW_LIMIT));
        }
        
        // Check if user has overdue books
        List<Borrow> overdueBorrows = borrowRepository.findByUserIdAndStatus(user.getId(), Borrow.BorrowStatus.OVERDUE);
        if (!overdueBorrows.isEmpty()) {
            throw new BusinessLogicException("User has overdue books and cannot borrow new books");
        }
    }

    @Transactional
    public BorrowResponse markBorrowAsPending(Long userId, Long bookId) {
        // Find active borrow record by user and book
        Borrow borrow = borrowRepository.findActiveBorrowByUserAndBook(userId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Active borrow record not found for user ID: " + userId + " and book ID: " + bookId));
        
        if (borrow.getStatus() != Borrow.BorrowStatus.ACTIVE) {
            throw new BusinessLogicException("Only active borrow records can be marked as pending");
        }
        
        borrow.setStatus(Borrow.BorrowStatus.REQUESTED);
        Borrow updatedBorrow = borrowRepository.save(borrow);
        return borrowMapper.toResponse(updatedBorrow);
    }

    @Transactional
    public BorrowResponse rejectBorrowRequest(Long userId, Long bookId) {
        // Find borrow record by user and book (can be pending or active)
        Borrow borrow = borrowRepository.findActiveBorrowByUserAndBook(userId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found for user ID: " + userId + " and book ID: " + bookId));
        
        if (borrow.getStatus() == Borrow.BorrowStatus.RETURNED || borrow.getStatus() == Borrow.BorrowStatus.REJECTED) {
            throw new BusinessLogicException("Cannot reject already returned or rejected borrow request");
        }

        if (borrow.getStatus() != Borrow.BorrowStatus.REQUESTED) {
            throw new BusinessLogicException("Only requested borrow records can be rejected");
        }

        // Return the reserved copy since request is being rejected
        bookService.incrementAvailableCopies(borrow.getBook().getId());

        borrow.setStatus(Borrow.BorrowStatus.REJECTED);
        Borrow updatedBorrow = borrowRepository.save(borrow);
        return borrowMapper.toResponse(updatedBorrow);
    }

    @Transactional
    public BorrowResponse acceptBorrowRequest(Long userId, Long bookId) {
        // Find requested borrow record by user and book
        Borrow borrow = borrowRepository.findActiveBorrowByUserAndBook(userId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow request not found for user ID: " + userId + " and book ID: " + bookId));
        
        if (borrow.getStatus() != Borrow.BorrowStatus.REQUESTED) {
            throw new BusinessLogicException("Only requested borrow records can be accepted");
        }
        
        // Check if book is still available
        if (borrow.getBook().getAvailableCopies() <= 0) {
            throw new BusinessLogicException("Book is no longer available for borrowing");
        }

        // Directly activate borrow upon acceptance
        borrow.setStatus(Borrow.BorrowStatus.ACTIVE);
        borrow.setBorrowDate(LocalDate.now());
        borrow.setDueDate(LocalDate.now().plusDays(BORROW_PERIOD_DAYS));

        Borrow updatedBorrow = borrowRepository.save(borrow);
        return borrowMapper.toResponse(updatedBorrow);
    }

    @Transactional
    public BorrowResponse activateBorrowRequest(Long userId, Long bookId) {
        // Find accepted borrow record by user and book
        Borrow borrow = borrowRepository.findActiveBorrowByUserAndBook(userId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Accepted borrow record not found for user ID: " + userId + " and book ID: " + bookId));
        
        if (borrow.getStatus() != Borrow.BorrowStatus.ACCEPTED) {
            throw new BusinessLogicException("Only accepted borrow records can be activated");
        }
        
        borrow.setStatus(Borrow.BorrowStatus.ACTIVE);
        borrow.setBorrowDate(LocalDate.now());
        borrow.setDueDate(LocalDate.now().plusDays(BORROW_PERIOD_DAYS));
        
        Borrow updatedBorrow = borrowRepository.save(borrow);
        return borrowMapper.toResponse(updatedBorrow);
    }
}

