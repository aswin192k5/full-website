package com.bill.backend.repository;

import com.bill.backend.model.DeviceCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceCommandRepository
        extends JpaRepository<DeviceCommand, String> {
}
