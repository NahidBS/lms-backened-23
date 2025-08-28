package com.library.management.service;

import com.library.management.dto.response.UserResponse;
import com.library.management.entity.User;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.mapper.UserMapper;
import com.library.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String role, Boolean active, Pageable pageable) {
        Page<User> users;
        if (role != null && active != null) {
            users = userRepository.findByRoleAndIsActive(role, active, pageable);
        } else if (role != null) {
            users = userRepository.findByRole(role, pageable);
        } else if (active != null) {
            users = userRepository.findByIsActive(active, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(userMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String query, Pageable pageable) {
        Page<User> users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, pageable);
        return users.map(userMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveBorrowers() {
        List<User> users = userRepository.findUsersWithActiveBorrows();
        return userMapper.toResponseList(users);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersWithOverdueBooks() {
        List<User> users = userRepository.findUsersWithOverdueBooks();
        return userMapper.toResponseList(users);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserStatistics(Long id) {
        User user = getUserEntityById(id);
        UserResponse response = userMapper.toResponse(user);
        
        return response;
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }
    
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }
    
    // Helper method for internal use by other services
    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
    
    @Transactional(readOnly = true)
    public long getActiveBorrowCount(Long userId) {
        return userRepository.countActiveBorrowsByUserId(userId);
    }
}

