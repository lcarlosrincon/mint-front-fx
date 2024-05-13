package com.mint.lc.demo.presenter;

import com.mint.lc.demo.EventContractor;
import com.mint.lc.demo.model.CreateEventApiService;
import com.mint.lc.demo.model.DeleteEventApiService;
import com.mint.lc.demo.model.EditEventApiService;
import com.mint.lc.demo.model.EventModel;
import com.mint.lc.demo.model.dto.EventRecord;
import com.mint.lc.demo.model.dto.EventRequest;
import com.mint.lc.demo.model.dto.Instructor;
import com.mint.lc.demo.view.EventScreen;
import javafx.concurrent.Service;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventPresenter implements EventContractor.Presenter {

    private static final Logger LOGGER = Logger.getLogger(EventPresenter.class.getName());

    private final EventContractor.View screen;
    private final EventContractor.Model model;
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

    public void saveEvent(LocalDate initialDate, LocalDate endDate, String description) {
        LOGGER.log(Level.INFO, "Save event " + initialDate + endDate + getSelectedEvent());
        Service<EventRecord> action = getSelectedEvent() != null ?
                new EditEventApiService(this.model.getInstructor().getId(), setNewValuesEvent(initialDate, endDate, description)) :
                new CreateEventApiService(this.model.getInstructor().getId(),
                        new EventRequest(initialDate, endDate, description, null));

        action.setOnSucceeded(event -> {
            EventRecord eventRecord = action.getValue();
            if (callback != null)
                callback.accept(eventRecord);
        });
        action.setOnFailed(event -> {
            LOGGER.log(Level.SEVERE, action.getException(), () -> "Failed to save event data: " + action.getException().getMessage());
            this.screen.displayExceptionAlert(Alert.AlertType.ERROR, "Exception has occurred", action.getException().getMessage());
        });
        action.start();
        LOGGER.log(Level.INFO, "Saving event has started");
    }

    private EventRecord setNewValuesEvent(LocalDate initialDate, LocalDate endDate, String description) {
        return new EventRecord(getSelectedEvent().getId(), initialDate, endDate, description);
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

    public void setSelectedEvent(EventRecord selectedItem) {
        LOGGER.info("Selected an event from the list:" + selectedItem);
        this.model.setSelectedEvent(selectedItem);
    }

    @Override
    public void deleteEvent(EventRecord selectedItem) {
        Service<EventRecord> action = new DeleteEventApiService(this.model.getInstructor().getId(), selectedItem.getId());
        action.setOnSucceeded(event -> {
            EventRecord eventRecord = action.getValue();
            if (callback != null)
                callback.accept(eventRecord);
        });
        action.setOnFailed(event -> {
            LOGGER.log(Level.SEVERE, action.getException(), () -> "Failed to delete event data: " + action.getException().getMessage());
            this.screen.displayExceptionAlert(Alert.AlertType.ERROR, "Exception has occurred", action.getException().getMessage());
        });
        action.start();
    }
}
