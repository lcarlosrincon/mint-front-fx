package com.mint.lc.demo.model.dto;

import java.time.LocalDate;

public class EventRecord {

    private String id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public EventRecord(String id, LocalDate initialDate, LocalDate endDate, String description) {
        this.id = id;
        this.startDate = initialDate;
        this.endDate = endDate;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "EventRecord{" +
                "initialDate=" + startDate +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                '}';
    }

    public boolean isThisDay(LocalDate atDay) {
        return atDay.compareTo(startDate) >= 0 && atDay.compareTo(endDate) <= 0;
    }
}
