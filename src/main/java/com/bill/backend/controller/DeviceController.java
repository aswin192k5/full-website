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

    // ===================== UTIL =====================
    private String normalizeMac(String mac) {
        return mac == null ? "" : mac.trim().toUpperCase().replace("-", ":");
    }

    // =================================================
    // ✅ POST /api/device/data  (ESP32 USES THIS)
    // =================================================
    @PostMapping("/data")
    public ResponseEntity<?> receiveData(
            @RequestParam("mac") String mac,
            @RequestBody EspData payload
    ) {
        try {
            String normalizedMac = normalizeMac(mac);

            EspData data = new EspData();
            data.setEspMac(normalizedMac);
            data.setVoltage(payload.getVoltage());
            data.setEnergyUsage(payload.getEnergyUsage());
            data.setPower(payload.getPower());
            data.setTemperature(payload.getTemperature());
            data.setHumidity(payload.getHumidity());
            // timestamp auto-generated (@CreationTimestamp)

            espDataRepository.save(data);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "espMac", normalizedMac
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save ESP data"));
        }
    }

    // =================================================
    // GET /api/device/latest/{mac}
    // =================================================
    @GetMapping("/latest/{espMac}")
    public ResponseEntity<?> getLatestData(@PathVariable String espMac) {

        String normalizedMac = normalizeMac(espMac);

        EspData latest =
                espDataRepository.findTopByEspMacOrderByTimestampDesc(normalizedMac);

        // ESP OFF / No data yet → SAFE RESPONSE
        if (latest == null) {
            return ResponseEntity.ok(Map.of(
                    "voltageV", 0,
                    "currentA", 0,
                    "powerW", 0,
                    "timestamp", 0
            ));
        }

        double voltage = latest.getVoltage() != null ? latest.getVoltage() : 0.0;
        double current = latest.getEnergyUsage() != null ? latest.getEnergyUsage() : 0.0;
        double power   = latest.getPower() != null ? latest.getPower() : voltage * current;
        long timestamp = latest.getTimestamp() != null
                ? latest.getTimestamp().getTime()
                : 0L;

        return ResponseEntity.ok(Map.of(
                "voltageV", voltage,
                "currentA", current,
                "powerW", power,
                "timestamp", timestamp
        ));
    }

    // =================================================
    // GET /api/device/status/{mac}
    // =================================================
    @GetMapping("/status/{espMac}")
    public ResponseEntity<?> getStatus(@PathVariable String espMac) {

        String normalizedMac = normalizeMac(espMac);

        EspData latest =
                espDataRepository.findTopByEspMacOrderByTimestampDesc(normalizedMac);

        if (latest == null || latest.getTimestamp() == null) {
            return ResponseEntity.ok(Map.of(
                    "onlineStatus", false,
                    "lastSeen", null
            ));
        }

        long lastSeen = latest.getTimestamp().getTime();
        boolean online = (System.currentTimeMillis() - lastSeen) < 15_000;

        return ResponseEntity.ok(Map.of(
                "onlineStatus", online,
                "lastSeen", lastSeen
        ));
    }
}
