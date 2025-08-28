package com.library.management.mapper;

import com.library.management.dto.response.DonationRequestResponse;
import com.library.management.entity.DonationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DonationRequestMapper {
    
    private final UserMapper userMapper;
    
    public DonationRequestResponse toResponse(DonationRequest donationRequest) {
        if (donationRequest == null) {
            return null;
        }
        
        Boolean canBeEdited = donationRequest.getStatus() == DonationRequest.DonationStatus.PENDING;
        Boolean canBeDeleted = donationRequest.getStatus() == DonationRequest.DonationStatus.PENDING || 
                              donationRequest.getStatus() == DonationRequest.DonationStatus.REJECTED;
        
        return DonationRequestResponse.builder()
                .id(donationRequest.getId())
                .user(userMapper.toResponse(donationRequest.getUser()))
                .bookTitle(donationRequest.getBookTitle())
                .author(donationRequest.getAuthor())
                .isbn(donationRequest.getIsbn())
                .description(donationRequest.getDescription())
                .status(donationRequest.getStatus())
                .adminNotes(donationRequest.getAdminNotes())
                .canBeEdited(canBeEdited)
                .canBeDeleted(canBeDeleted)
                .createdAt(donationRequest.getCreatedAt())
                .updatedAt(donationRequest.getUpdatedAt())
                .build();
    }
    
    public List<DonationRequestResponse> toResponseList(List<DonationRequest> donationRequests) {
        if (donationRequests == null) {
            return null;
        }
        
        return donationRequests.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}

