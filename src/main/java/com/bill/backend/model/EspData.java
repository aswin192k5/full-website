package com.bill.backend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "esp_data")
public class EspData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "esp_mac", nullable = false, length = 100)
    private String espMac;  // MAC of ESP32

    @Column(name = "temperature", nullable = true)
    private Double temperature;

    @Column(name = "humidity", nullable = true)
    private Double humidity;

    @Column(name = "voltage", nullable = true)
    private Double voltage;

    // Reusing as "current"/energy metric
    @Column(name = "energy_usage", nullable = true)
    private Double energyUsage;

    @Column(name = "power", nullable = true)
    private Double power;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp", updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private Date timestamp;

    public EspData() {}

    public Long getId() {
        return id;
    }

    public String getEspMac() {
        return espMac;
    }

    public void setEspMac(String espMac) {
        this.espMac = espMac;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    public Double getEnergyUsage() {
        return energyUsage;
    }

    public void setEnergyUsage(Double energyUsage) {
        this.energyUsage = energyUsage;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
