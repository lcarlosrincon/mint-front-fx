package com.mint.lc.demo;

import java.time.LocalDate;

public class EventRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String eventType;

    public EventRequest(LocalDate startDate, LocalDate endDate, String description, String eventType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.eventType = eventType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "EventRequest{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}

