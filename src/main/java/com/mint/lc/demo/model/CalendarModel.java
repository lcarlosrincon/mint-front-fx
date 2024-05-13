package com.mint.lc.demo.model;

import com.mint.lc.demo.CalendarContractor;
import com.mint.lc.demo.model.dto.EventRecord;
import com.mint.lc.demo.model.dto.Instructor;

import java.time.YearMonth;
import java.util.List;

public class CalendarModel implements CalendarContractor.Model {

    private Instructor instructor;
    private YearMonth currentYearMonth;
    private List<EventRecord> events;

    public CalendarModel(Instructor instructor, YearMonth currentYearMonth, List<EventRecord> events) {
        this.instructor = instructor;
        this.currentYearMonth = currentYearMonth;
        this.events = events;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public YearMonth getCurrentYearMonth() {
        return currentYearMonth;
    }

    public void setCurrentYearMonth(YearMonth currentYearMonth) {
        this.currentYearMonth = currentYearMonth;
    }

    public List<EventRecord> getEvents() {
        return events;
    }

    public void setEvents(List<EventRecord> events) {
        this.events = events;
    }
}
