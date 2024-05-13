package com.mint.lc.demo.view;

import com.mint.lc.demo.EventContractor;
import com.mint.lc.demo.model.dto.EventRecord;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

public class EventScreen implements EventContractor.View {

    public static final DateTimeFormatter EVENT_LIST_DAY_FORMAT = DateTimeFormatter.ofPattern("EEE dd");

    private final EventContractor.Presenter presenter;

    public EventScreen(EventContractor.Presenter presenter) {
        this.presenter = presenter;
    }

    public Dialog<ButtonType> build() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Event Details");
        dialog.setWidth(600);
        dialog.setHeaderText("Event Scheduled for " + this.presenter.getSelectedDate());

        // Create initial date picker
        DatePicker initialDatePicker = new DatePicker(this.presenter.getSelectedDate());
        StringConverter<LocalDate> stringConverter = new StringConverter<>() {
            @Override
            public String toString(LocalDate object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ? LocalDate.parse(string) : null;
            }
        };
        initialDatePicker.setConverter(stringConverter);
        initialDatePicker.getEditor().focusedProperty().addListener(buildFocusListener(initialDatePicker));

        DatePicker endDatePicker = new DatePicker(this.presenter.getSelectedDate());
        endDatePicker.setConverter(stringConverter);
        endDatePicker.getEditor().focusedProperty().addListener(buildFocusListener(endDatePicker));

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Event Description");

        ButtonType createEventType = new ButtonType("Create Event");

        dialog.getDialogPane().getButtonTypes().addAll(createEventType, ButtonType.CANCEL);

        ListView<EventRecord> eventListView = new ListView<>();
        eventListView.setPrefHeight(100);
        List<EventRecord> eventsOfDay = this.presenter.getEventsByDate();
        if (eventsOfDay != null && !eventsOfDay.isEmpty())
            eventListView.getItems().addAll(eventsOfDay);

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/google.png")));
        ImageView googleIcon = new ImageView(icon);
        googleIcon.setFitWidth(16);
        googleIcon.setFitHeight(16);

        eventListView.setCellFactory(param -> new ListCell<>() {
            private final Hyperlink editLink = new Hyperlink("Edit");
            private final Hyperlink deleteLink = new Hyperlink("Delete");

            {
                editLink.setOnAction(event -> {
                    EventRecord selectedItem = getItem();
                    if (selectedItem != null) {
                        presenter.setSelectedEvent(selectedItem);
                        initialDatePicker.setValue(selectedItem.getStartDate());
                        endDatePicker.setValue(selectedItem.getEndDate());
                        descriptionField.setText(selectedItem.getDescription());
                        Platform.runLater(initialDatePicker::requestFocus);
                    }
                });

                deleteLink.setOnAction(event -> {
                    EventRecord selectedItem = getItem();
                    if (selectedItem != null) {
                        presenter.deleteEvent(selectedItem);
                        eventListView.getItems().remove(selectedItem);
                    }
                });
            }

            @Override
            protected void updateItem(EventRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(" (" + EVENT_LIST_DAY_FORMAT.format(item.getStartDate()) + " - " + EVENT_LIST_DAY_FORMAT.format(item.getEndDate()) + ") " + item.getDescription());
                    if (item.getExternalId() != null)
                        setGraphic(new HBox(5, editLink, deleteLink, googleIcon));
                    else
                        setGraphic(new HBox(5, editLink, deleteLink));
                }
            }
        });

        // Populate fields if editing an existing event
        EventRecord selectedEvent = getSelectedEvent();
        if (selectedEvent != null) {
            initialDatePicker.setValue(selectedEvent.getStartDate());
            endDatePicker.setValue(selectedEvent.getEndDate());
            descriptionField.setText(selectedEvent.getDescription());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        grid.add(new Label("Events for " + this.presenter.getSelectedDate()), 0, 0);
        grid.add(eventListView, 0, 1, 2, 1);
        grid.add(new Label("Initial Date:"), 0, 2);
        grid.add(initialDatePicker, 1, 2);
        grid.add(new Label("End Date:"), 0, 3);
        grid.add(endDatePicker, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionField, 1, 4);

        Node createEventButton = dialog.getDialogPane().lookupButton(createEventType);
        createEventButton.setDisable(true);

        initialDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                createEventButton.setDisable(newValue == null || endDatePicker.getValue() == null || descriptionField.getText().isEmpty()));

        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                createEventButton.setDisable(newValue == null || initialDatePicker.getValue() == null || descriptionField.getText().isEmpty()));
        descriptionField.textProperty().addListener((observable, oldValue, newValue) ->
                createEventButton.setDisable(newValue.isEmpty() || initialDatePicker.getValue() == null || endDatePicker.getValue() == null));

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(initialDatePicker::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createEventType) {
                return new ButtonType("Create Event", ButtonBar.ButtonData.OK_DONE);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            if (result.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                LocalDate initialDate = initialDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                String description = descriptionField.getText();
                if (endDate != null && initialDate != null && endDate.compareTo(initialDate) >= 0)
                    this.presenter.saveEvent(initialDate, endDate, description);
                else
                    displayExceptionAlert(Alert.AlertType.WARNING, "Validation Dates", "Please, check the end date is after of start date");
            }
        });
        return dialog;
    }

    private EventRecord getSelectedEvent() {
        return this.presenter.getSelectedEvent();
    }

    private ChangeListener<Boolean> buildFocusListener(DatePicker datePicker) {
        return (obj, wasFocused, isFocused) -> {
            if (!isFocused) {
                try {
                    datePicker.setValue(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                    datePicker.getEditor().setText(datePicker.getConverter().toString(datePicker.getValue()));
                }
            }
        };
    }

    public void displayExceptionAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
