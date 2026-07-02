package com.rh.conges.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application-specific configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationConfig {

    private String name = "Leave Management System";
    private String url = "http://localhost:8080";
    private int leaveRequestReminderDays = 3;
    private int lowBalanceThreshold = 5;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLeaveRequestReminderDays() {
        return leaveRequestReminderDays;
    }

    public void setLeaveRequestReminderDays(int leaveRequestReminderDays) {
        this.leaveRequestReminderDays = leaveRequestReminderDays;
    }

    public int getLowBalanceThreshold() {
        return lowBalanceThreshold;
    }

    public void setLowBalanceThreshold(int lowBalanceThreshold) {
        this.lowBalanceThreshold = lowBalanceThreshold;
    }
}

