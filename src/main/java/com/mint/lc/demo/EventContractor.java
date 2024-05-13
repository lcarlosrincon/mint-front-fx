package com.mint.lc.demo;

import com.mint.lc.demo.model.dto.EventRecord;
import com.mint.lc.demo.model.dto.Instructor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public interface EventContractor {

    interface View {
        Dialog<ButtonType> build();

        void displayExceptionAlert(Alert.AlertType alertType, String title, String message);
    }

    interface Presenter {
        void start(Consumer<EventRecord> callback);

        void saveEvent(LocalDate initialDate, LocalDate endDate, String description);

        List<EventRecord> getEventsByDate();

        LocalDate getSelectedDate();

        EventRecord getSelectedEvent();

        void setSelectedEvent(EventRecord selectedItem);

        void deleteEvent(EventRecord selectedItem);
    }

    interface Model {
        List<EventRecord> getEventsByDay();

        LocalDate getSelectedDate();

        EventRecord getSelectedEvent();

        void setSelectedEvent(EventRecord selectedEvent);

        Instructor getInstructor();
    }
}
