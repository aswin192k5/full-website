package com.bill.backend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "device_commands")
public class DeviceCommand {

    @Id
    @Column(length = 50)
    private String espMac;

    private String relay1;
    private String relay2;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // ---- getters & setters ----

    public String getEspMac() {
        return espMac;
    }

    public void setEspMac(String espMac) {
        this.espMac = espMac;
    }

    public String getRelay1() {
        return relay1;
    }

    public void setRelay1(String relay1) {
        this.relay1 = relay1;
    }

    public String getRelay2() {
        return relay2;
    }

    public void setRelay2(String relay2) {
        this.relay2 = relay2;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
