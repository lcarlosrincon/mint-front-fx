package com.mint.lc.demo.model;

import com.mint.lc.demo.model.dto.EventRecord;
import com.mint.lc.demo.model.dto.Instructor;

import java.time.LocalDate;
import java.util.List;

public class EventModel {

    private List<EventRecord> eventsByDay;
    private LocalDate selectedDate;
    private EventRecord selectedEvent;

    private Instructor instructor;

    public EventModel(List<EventRecord> eventsByDay, LocalDate selectedDate, EventRecord selectedEvent, Instructor instructor) {
        this.eventsByDay = eventsByDay;
        this.selectedDate = selectedDate;
        this.selectedEvent = selectedEvent;
        this.instructor = instructor;
    }

    public List<EventRecord> getEventsByDay() {
        return eventsByDay;
    }

    public void setEventsByDay(List<EventRecord> eventsByDay) {
        this.eventsByDay = eventsByDay;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public EventRecord getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(EventRecord selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }
}
