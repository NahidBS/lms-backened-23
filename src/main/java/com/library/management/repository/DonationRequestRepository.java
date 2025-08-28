package com.library.management.repository;

import com.library.management.entity.DonationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRequestRepository extends JpaRepository<DonationRequest, Long> {
    
    List<DonationRequest> findByUserId(Long userId);
    
    List<DonationRequest> findByStatus(DonationRequest.DonationStatus status);
    
    @Query("SELECT d FROM DonationRequest d WHERE d.user.id = :userId AND d.status = :status")
    List<DonationRequest> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") DonationRequest.DonationStatus status);
    
    @Query("SELECT d FROM DonationRequest d ORDER BY d.createdAt DESC")
    List<DonationRequest> findAllOrderByCreatedAtDesc();
}

