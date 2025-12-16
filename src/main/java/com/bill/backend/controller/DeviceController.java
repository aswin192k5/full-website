package com.bill.backend.controller;

import com.bill.backend.model.EspData;
import com.bill.backend.repository.EspDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private EspDataRepository espDataRepository;

    // GET /api/device/latest/{mac}
    @GetMapping("/latest/{espMac}")
    public ResponseEntity<?> getLatestData(@PathVariable String espMac) {
        EspData latest = espDataRepository.findTopByEspMacOrderByTimestampDesc(espMac);
        if (latest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No data found for ESP MAC: " + espMac);
        }

        double voltage = latest.getVoltage() != null ? latest.getVoltage() : 0.0;
        double current = latest.getEnergyUsage() != null ? latest.getEnergyUsage() : 0.0;
        double power   = latest.getPower() != null ? latest.getPower() : voltage * current;
        long   ts      = latest.getTimestamp() != null ? latest.getTimestamp().getTime() : 0L;

        return ResponseEntity.ok(new Object() {
            public final Double voltageV = voltage;
            public final Double currentA = current;
            public final Double powerW   = power;
            public final Long   timestamp = ts;
        });
    }

    // GET /api/device/status/{mac}
    @GetMapping("/status/{espMac}")
    public ResponseEntity<?> getStatus(@PathVariable String espMac) {
        EspData latest = espDataRepository.findTopByEspMacOrderByTimestampDesc(espMac);
        if (latest == null) {
            return ResponseEntity.ok(new Object() {
                public final boolean onlineStatus = false;
                public final Long lastSeen = null;
            });
        }

        long now  = System.currentTimeMillis();
        long last = latest.getTimestamp() != null ? latest.getTimestamp().getTime() : 0L;
        boolean online = (now - last) < 15_000;

        return ResponseEntity.ok(new Object() {
            public final boolean onlineStatus = online;
            public final Long lastSeen = last;
        });
    }
}
