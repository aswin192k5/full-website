package com.bill.backend.controller;

import com.bill.backend.model.EspData;
import com.bill.backend.repository.EspDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/device")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private EspDataRepository espDataRepository;

    // Normalize MAC (very important)
    private String normalizeMac(String mac) {
        return mac.trim().toUpperCase().replace("-", ":");
    }

    // GET /api/device/latest/{mac}
    @GetMapping("/latest/{espMac}")
    public ResponseEntity<?> getLatestData(@PathVariable String espMac) {

        String normalizedMac = normalizeMac(espMac);

        EspData latest = espDataRepository
                .findTopByEspMacOrderByTimestampDesc(normalizedMac);

        if (latest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No data found", "espMac", normalizedMac));
        }

        double voltage = latest.getVoltage() != null ? latest.getVoltage() : 0.0;
        double current = latest.getEnergyUsage() != null ? latest.getEnergyUsage() : 0.0;
        double power   = latest.getPower() != null ? latest.getPower() : voltage * current;
        long timestamp = latest.getTimestamp() != null ? latest.getTimestamp().getTime() : 0L;

        return ResponseEntity.ok(Map.of(
                "voltageV", voltage,
                "currentA", current,
                "powerW", power,
                "timestamp", timestamp
        ));
    }

    // GET /api/device/status/{mac}
    @GetMapping("/status/{espMac}")
    public ResponseEntity<?> getStatus(@PathVariable String espMac) {

        String normalizedMac = normalizeMac(espMac);

        EspData latest = espDataRepository
                .findTopByEspMacOrderByTimestampDesc(normalizedMac);

        if (latest == null) {
            return ResponseEntity.ok(Map.of(
                    "onlineStatus", false,
                    "lastSeen", null
            ));
        }

        long now = System.currentTimeMillis();
        long last = latest.getTimestamp().getTime();
        boolean online = (now - last) < 15_000;

        return ResponseEntity.ok(Map.of(
                "onlineStatus", online,
                "lastSeen", last
        ));
    }
}
