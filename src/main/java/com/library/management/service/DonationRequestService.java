package com.library.management.service;

import com.library.management.dto.request.DonationRequestCreateRequest;
import com.library.management.dto.request.DonationRequestUpdateRequest;
import com.library.management.dto.request.DonationStatusUpdateRequest;
import com.library.management.dto.response.DonationRequestResponse;
import com.library.management.entity.DonationRequest;
import com.library.management.entity.User;
import com.library.management.exception.BusinessLogicException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.mapper.DonationRequestMapper;
import com.library.management.repository.DonationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationRequestService {
    
    private final DonationRequestRepository donationRequestRepository;
    private final DonationRequestMapper donationRequestMapper;
    private final UserService userService;
    
    @Transactional
    public DonationRequestResponse createDonationRequest(DonationRequestCreateRequest request) {
        // Verify user exists
        User user = userService.getUserEntityById(request.getUserId());
        
        DonationRequest donationRequest = DonationRequest.builder()
                .user(user)
                .bookTitle(request.getBookTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .description(request.getDescription())
                .status(DonationRequest.DonationStatus.PENDING)
                .build();
        
        DonationRequest savedRequest = donationRequestRepository.save(donationRequest);
        return donationRequestMapper.toResponse(savedRequest);
    }
    
    @Transactional(readOnly = true)
    public DonationRequestResponse getDonationRequestById(Long id) {
        DonationRequest donationRequest = donationRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DonationRequest", "id", id));
        return donationRequestMapper.toResponse(donationRequest);
    }
    
    @Transactional(readOnly = true)
    public Page<DonationRequestResponse> getAllDonationRequests(Long userId, String status, Pageable pageable) {
        Page<DonationRequest> donationRequests = donationRequestRepository.findAll(pageable);
        return donationRequests.map(donationRequestMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public List<DonationRequestResponse> getUserDonationRequests(Long userId, String status) {
        // Verify user exists
        userService.getUserEntityById(userId);
        
        List<DonationRequest> donationRequests;
        if (status != null) {
            DonationRequest.DonationStatus donationStatus = DonationRequest.DonationStatus.valueOf(status.toUpperCase());
            donationRequests = donationRequestRepository.findByUserIdAndStatus(userId, donationStatus);
        } else {
            donationRequests = donationRequestRepository.findByUserId(userId);
        }
        
        return donationRequestMapper.toResponseList(donationRequests);
    }
    
    @Transactional(readOnly = true)
    public List<DonationRequestResponse> getPendingDonationRequests() {
        List<DonationRequest> donationRequests = donationRequestRepository.findByStatus(DonationRequest.DonationStatus.PENDING);
        return donationRequestMapper.toResponseList(donationRequests);
    }
    
    @Transactional(readOnly = true)
    public List<DonationRequestResponse> getApprovedDonationRequests() {
        List<DonationRequest> donationRequests = donationRequestRepository.findByStatus(DonationRequest.DonationStatus.APPROVED);
        return donationRequestMapper.toResponseList(donationRequests);
    }
    
    @Transactional
    public DonationRequestResponse updateDonationRequest(Long id, DonationRequestUpdateRequest request) {
        DonationRequest donationRequest = donationRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DonationRequest", "id", id));
        
        // Only allow updates for pending requests
        if (donationRequest.getStatus() != DonationRequest.DonationStatus.PENDING) {
            throw new BusinessLogicException("Only pending donation requests can be updated");
        }
        
        donationRequest.setBookTitle(request.getBookTitle());
        donationRequest.setAuthor(request.getAuthor());
        donationRequest.setIsbn(request.getIsbn());
        donationRequest.setDescription(request.getDescription());
        
        DonationRequest updatedRequest = donationRequestRepository.save(donationRequest);
        return donationRequestMapper.toResponse(updatedRequest);
    }
    
    @Transactional
    public void deleteDonationRequest(Long id) {
        DonationRequest donationRequest = donationRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DonationRequest", "id", id));
        
        // Only allow deletion for pending or rejected requests
        if (donationRequest.getStatus() == DonationRequest.DonationStatus.APPROVED || 
            donationRequest.getStatus() == DonationRequest.DonationStatus.RECEIVED) {
            throw new BusinessLogicException("Cannot delete approved or received donation requests");
        }
        
        donationRequestRepository.delete(donationRequest);
    }
    
    @Transactional
    public DonationRequestResponse updateDonationRequestStatus(Long id, DonationStatusUpdateRequest request) {
        DonationRequest donationRequest = donationRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DonationRequest", "id", id));
        
        // Only allow status updates for pending requests
        if (donationRequest.getStatus() != DonationRequest.DonationStatus.PENDING) {
            throw new BusinessLogicException("Only pending donation requests can have their status updated");
        }
        
        // Convert boolean to enum (false = REJECTED, true = APPROVED)
        DonationRequest.DonationStatus newStatus = request.getStatus() ? 
                DonationRequest.DonationStatus.APPROVED : DonationRequest.DonationStatus.REJECTED;
        
        donationRequest.setStatus(newStatus);
        donationRequest.setAdminNotes(request.getAdminNotes());
        
        DonationRequest updatedRequest = donationRequestRepository.save(donationRequest);
        return donationRequestMapper.toResponse(updatedRequest);
    }

    @Transactional
    public DonationRequestResponse approveDonationRequest(Long id, String adminNotes) {
        DonationRequest donationRequest = donationRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DonationRequest", "id", id));
        
        // Only allow approval for pending requests
        if (donationRequest.getStatus() != DonationRequest.DonationStatus.PENDING) {
            throw new BusinessLogicException("Only pending donation requests can be approved");
        }
        
        donationRequest.setStatus(DonationRequest.DonationStatus.APPROVED);
        if (adminNotes != null) {
            donationRequest.setAdminNotes(adminNotes);
        }
        
        DonationRequest updatedRequest = donationRequestRepository.save(donationRequest);
        return donationRequestMapper.toResponse(updatedRequest);
    }

    @Transactional
    public DonationRequestResponse rejectDonationRequest(Long id, String adminNotes) {
        DonationRequest donationRequest = donationRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DonationRequest", "id", id));
        
        // Only allow rejection for pending requests
        if (donationRequest.getStatus() != DonationRequest.DonationStatus.PENDING) {
            throw new BusinessLogicException("Only pending donation requests can be rejected");
        }
        
        donationRequest.setStatus(DonationRequest.DonationStatus.REJECTED);
        if (adminNotes != null) {
            donationRequest.setAdminNotes(adminNotes);
        }
        
        DonationRequest updatedRequest = donationRequestRepository.save(donationRequest);
        return donationRequestMapper.toResponse(updatedRequest);
    }
}

