package com.mint.lc.demo.presenter;

import com.mint.lc.demo.model.CreateEventApiService;
import com.mint.lc.demo.model.EventModel;
import com.mint.lc.demo.model.dto.EventRecord;
import com.mint.lc.demo.model.dto.EventRequest;
import com.mint.lc.demo.model.dto.Instructor;
import com.mint.lc.demo.view.EventScreen;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventPresenter {

    private static final Logger LOGGER = Logger.getLogger(EventPresenter.class.getName());

    private final EventScreen screen;
    private final EventModel model;
    private Consumer<EventRecord> callback;

    public EventPresenter(List<EventRecord> eventsByDay, LocalDate selectedDate, Instructor instructor) {
        this.screen = new EventScreen(this);
        this.model = new EventModel(eventsByDay, selectedDate, null, instructor);
    }

    public void start(Consumer<EventRecord> callback) {
        // Build and start dialog
        this.screen.build();
        this.callback = callback;
    }

    public void createEvent(LocalDate initialDate, LocalDate endDate, String description) {
        LOGGER.log(Level.INFO, "Create event" + initialDate + endDate);
        CreateEventApiService eventAPIService = new CreateEventApiService(this.model.getInstructor().getId(),
                new EventRequest(initialDate, endDate, description, "abc1"));
        eventAPIService.setOnSucceeded(event -> {
            EventRecord eventRecord = eventAPIService.getValue();
            if (callback != null)
                callback.accept(eventRecord);
        });
        eventAPIService.setOnFailed(event -> {
            LOGGER.log(Level.SEVERE, eventAPIService.getException(), () -> "Failed to save event data: " + eventAPIService.getException().getMessage());
            this.screen.displayExceptionAlert(Alert.AlertType.ERROR, "Exception has occurred", eventAPIService.getException().getMessage());
        });
        eventAPIService.start();
        LOGGER.log(Level.INFO, "Saving event has started");
    }

    public LocalDate getSelectedDate() {
        return this.model.getSelectedDate();
    }

    public List<EventRecord> getEventsByDate() {
        return this.model.getEventsByDay();
    }

    public EventRecord getSelectedEvent() {
        return this.model.getSelectedEvent();
    }
}
