package com.mint.lc.demo;

import java.time.LocalDate;

public class EventRecord {
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public EventRecord(LocalDate initialDate, LocalDate endDate, String description) {
        this.startDate = initialDate;
        this.endDate = endDate;
        this.description = description;
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
