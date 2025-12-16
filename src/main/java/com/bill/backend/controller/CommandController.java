package com.bill.backend.controller;

import com.bill.backend.model.DeviceCommand;
import com.bill.backend.repository.DeviceCommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
@RestController
@RequestMapping("/api/command")
@CrossOrigin(origins = "*")
public class CommandController {

    @Autowired
    private DeviceCommandRepository repo;

    @GetMapping
    public ResponseEntity<DeviceCommand> getCommand(@RequestParam String mac) {
        DeviceCommand cmd = repo.findById(mac).orElseGet(() -> {
            DeviceCommand dc = new DeviceCommand();
            dc.setEspMac(mac);
            dc.setRelay1("off");
            dc.setRelay2("off");
            return dc;
        });
        return ResponseEntity.ok(cmd);
    }

    @PostMapping
    public ResponseEntity<String> setCommand(@RequestBody DeviceCommand cmd) {
        cmd.setUpdatedAt(new java.util.Date());
        repo.save(cmd);
        return ResponseEntity.ok("Command saved");
    }
}
