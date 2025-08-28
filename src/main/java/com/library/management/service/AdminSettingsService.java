package com.library.management.service;

import com.library.management.dto.response.AdminSettingResponse;
import com.library.management.entity.AdminSettings;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.AdminSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminSettingsService {
    
    private final AdminSettingsRepository adminSettingsRepository;
    
    @Transactional
    public AdminSettingResponse setBorrowDayLimit(Integer value) {
        AdminSettings settings = getOrCreateAdminSettings();
        settings.setBorrowDayLimit(value);
        adminSettingsRepository.save(settings);
        return mapToResponse(settings);
    }
    
    @Transactional
    public AdminSettingResponse setBorrowExtendLimit(Integer value) {
        AdminSettings settings = getOrCreateAdminSettings();
        settings.setBorrowExtendLimit(value);
        adminSettingsRepository.save(settings);
        return mapToResponse(settings);
    }
    
    @Transactional
    public AdminSettingResponse setBorrowBookLimit(Integer value) {
        AdminSettings settings = getOrCreateAdminSettings();
        settings.setBorrowBookLimit(value);
        adminSettingsRepository.save(settings);
        return mapToResponse(settings);
    }
    
    @Transactional
    public AdminSettingResponse setBookingDaysLimit(Integer value) {
        AdminSettings settings = getOrCreateAdminSettings();
        settings.setBookingDaysLimit(value);
        adminSettingsRepository.save(settings);
        return mapToResponse(settings);
    }
    
    @Transactional(readOnly = true)
    public AdminSettingResponse getAllSettings() {
        AdminSettings settings = getOrCreateAdminSettings();
        return mapToResponse(settings);
    }
    
    private AdminSettings getOrCreateAdminSettings() {
        return adminSettingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    AdminSettings defaultSettings = AdminSettings.builder()
                            .borrowDayLimit(14)
                            .borrowExtendLimit(2)
                            .borrowBookLimit(5)
                            .bookingDaysLimit(7)
                            .build();
                    return adminSettingsRepository.save(defaultSettings);
                });
    }
    
    private AdminSettingResponse mapToResponse(AdminSettings settings) {
        return AdminSettingResponse.builder()
                .borrowDayLimit(settings.getBorrowDayLimit())
                .borrowExtendLimit(settings.getBorrowExtendLimit())
                .borrowBookLimit(settings.getBorrowBookLimit())
                .bookingDaysLimit(settings.getBookingDaysLimit())
                .build();
    }
}

