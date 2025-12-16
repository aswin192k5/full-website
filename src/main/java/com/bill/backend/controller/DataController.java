package com.bill.backend.controller;

import com.bill.backend.model.EspData;
import com.bill.backend.repository.EspDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataController {

    @Autowired
    private EspDataRepository espDataRepository;

    // ESP32 sends data here: POST /api/data?mac=XX:XX...
    @PostMapping
    public ResponseEntity<String> saveEspData(
            @RequestParam(required = false) String mac,
            @RequestBody(required = false) EspData body) {

        // ESP32 code sends JSON with fields: voltage, current, power, tamper, reed, ip, ts
        // mac is in query param "mac"
        if (mac == null || mac.isBlank()) {
            return ResponseEntity.badRequest().body("Missing mac");
        }
        if (body == null) {
            body = new EspData();
        }

        EspData data = new EspData();
        data.setEspMac(mac);
        data.setVoltage(body.getVoltage());
        data.setEnergyUsage(body.getEnergyUsage()); // optional, if you send it
        data.setPower(body.getPower());

        espDataRepository.save(data);
        return ResponseEntity.ok("ESP data saved successfully");
    }
}
