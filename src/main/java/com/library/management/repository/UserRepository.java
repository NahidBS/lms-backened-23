package com.library.management.repository;

import com.library.management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    Page<User> findByRole(String role, Pageable pageable);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    
    Page<User> findByRoleAndIsActive(String role, Boolean isActive, Pageable pageable);
    
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);
    
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String email, Pageable pageable);
    
    @Query("SELECT DISTINCT u FROM User u JOIN u.borrows b WHERE b.returnDate IS NULL")
    List<User> findUsersWithActiveBorrows();
    
    @Query("SELECT DISTINCT u FROM User u JOIN u.borrows b WHERE b.dueDate < CURRENT_DATE AND b.returnDate IS NULL")
    List<User> findUsersWithOverdueBooks();
    
    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.user.id = :userId AND b.returnDate IS NULL")
    long countActiveBorrowsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.user.id = :userId")
    long countTotalBorrowsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(b) FROM Borrow b WHERE b.user.id = :userId AND b.dueDate < CURRENT_DATE AND b.returnDate IS NULL")
    long countOverdueBorrowsByUserId(@Param("userId") Long userId);
}

