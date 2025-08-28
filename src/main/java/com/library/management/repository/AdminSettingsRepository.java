package com.library.management.repository;

import com.library.management.entity.AdminSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminSettingsRepository extends JpaRepository<AdminSettings, Long> {
    
    Optional<AdminSettings> findFirstByOrderByIdAsc();
}

