package com.mint.lc.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EventSchedulerApp {

    private static final Logger LOGGER = Logger.getLogger(EventSchedulerApp.class.getName());
    public static final DateTimeFormatter EVENT_LIST_DAY_FORMAT = DateTimeFormatter.ofPattern("EEE dd");

    private YearMonth currentYearMonth;
    private GridPane calendarGrid;
    private List<EventRecord> events;

    private void loadLoggingProperties() {
        try (InputStream is = getClass().getResourceAsStream("/logger.properties")) {
            if (is != null) {
                LogManager.getLogManager().readConfiguration(is);
            } else {
                System.err.println("Failed to load logging properties file.");
            }
        } catch (IOException e) {
            System.err.println("Error loading logging properties: " + e.getMessage());
        }
    }

    public void start(Stage primaryStage) {
        loadLoggingProperties();
        currentYearMonth = YearMonth.now();
        events = List.of();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setBackground(LoginScreen.BACKGROUND);

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(0, 0, 10, 0));

        Label monthLabel = new Label();
        monthLabel.setTextFill(Color.WHITE);
        updateMonthLabel(monthLabel);

        HBox navigationBox = new HBox(10);
        navigationBox.setAlignment(Pos.CENTER);

        Button previousMonthButton = new Button("<< Previous Month");
        previousMonthButton.setOnAction(event -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateMonthLabel(monthLabel);
            updateCalendar();
        });

        Button nextMonthButton = new Button("Next Month >>");
        nextMonthButton.setOnAction(event -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateMonthLabel(monthLabel);
            updateCalendar();
        });

        navigationBox.getChildren().addAll(previousMonthButton, nextMonthButton);
        topBox.getChildren().addAll(monthLabel, navigationBox);
        root.setTop(topBox);

        calendarGrid = new GridPane();
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setAlignment(Pos.CENTER);

        // Add header row with days of the week
        addHeaderRow();

        updateCalendar();

        root.setCenter(calendarGrid);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Monthly Event Scheduler");
        primaryStage.show();
    }

    private void updateMonthLabel(Label monthLabel) {
        String monthText = currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear();
        monthLabel.setText(monthText);
    }

    private void updateCalendar() {
        // paint empty calendar
        this.events = List.of();
        this.updateGridCalendar();
        GetEventsApiService eventAPIService = new GetEventsApiService("111", currentYearMonth);
        eventAPIService.setOnSucceeded(event -> {
            List<EventRecord> eventRecords = eventAPIService.getValue();
            // Process the fetched event records
            // eventListView.getItems().addAll(eventRecords);
            if (eventRecords.isEmpty()) {
                displayExceptionAlert(Alert.AlertType.INFORMATION, "Create Events", "You don't have events created");
            } else {
                processListEvents(eventRecords);
            }
        });
        eventAPIService.setOnFailed(event -> {
            LOGGER.log(Level.SEVERE, "Failed to fetch event data: " + eventAPIService.getException().getMessage());
            displayExceptionAlert(Alert.AlertType.ERROR, "Exception has occurred", eventAPIService.getException().getMessage());
        });
        eventAPIService.start();
    }

    private void processListEvents(List<EventRecord> eventRecords) {
        this.events = eventRecords;
        this.updateGridCalendar();
    }

    private Map<LocalDate, List<EventRecord>> getEventMap(List<EventRecord> eventRecords) {
        return IntStream.rangeClosed(1, currentYearMonth.lengthOfMonth()).boxed()
                .collect(Collectors.toMap(day -> currentYearMonth.atDay(day),
                        day -> getEvents(currentYearMonth.atDay(day), eventRecords),
                        (d1, d2) -> d1));
    }

    private void processEventCreated(EventRecord eventRecord) {
        this.events = Stream.concat(this.events.stream(), Stream.of(eventRecord)).collect(Collectors.toList());
        this.updateGridCalendar();
    }

    private List<EventRecord> getEvents(LocalDate atDay, List<EventRecord> events) {
        return events.stream().filter(event -> event.isThisDay(atDay)).collect(Collectors.toList());
    }

    private void updateGridCalendar() {
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) > 0); // Remove existing calendar rows

        int daysInMonth = currentYearMonth.lengthOfMonth();
        int firstDayOfWeek = currentYearMonth.atDay(1).getDayOfWeek().getValue();
        Map<LocalDate, List<EventRecord>> eventsMap = getEventMap(this.events);

        for (int i = 1; i <= daysInMonth; i++) {
            Button dayButton = new Button(Integer.toString(i));
            dayButton.setPrefWidth(50);
            dayButton.setPrefHeight(50);

            LocalDate currentDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonthValue(), i);
            int numEvents = eventsMap.getOrDefault(currentDate, List.of()).size();
            if (numEvents > 0) {
                dayButton.getStyleClass().add("event-button");

                Circle eventBubble = new Circle(8, getColorForEventCount(numEvents));
                javafx.scene.text.Text numEventsText = new javafx.scene.text.Text(Integer.toString(numEvents));
                numEventsText.setFill(Color.WHITE);

                StackPane stackPane = new StackPane(dayButton);
                stackPane.setAlignment(Pos.TOP_CENTER);
                stackPane.getChildren().addAll(eventBubble, numEventsText);

                calendarGrid.add(stackPane, (firstDayOfWeek + i - 2) % 7, (firstDayOfWeek + i - 2) / 7 + 1);
            } else {
                calendarGrid.add(dayButton, (firstDayOfWeek + i - 2) % 7, (firstDayOfWeek + i - 2) / 7 + 1);
            }
            GridPane.setHalignment(dayButton, HPos.CENTER);

            dayButton.setOnAction(event -> showEventDialog(currentDate, null));
        }
    }

    private Color getColorForEventCount(int numEvents) {
        int color = Math.min(numEvents * 10, 100) * 255 / 100;
        Random random = new Random(color);
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private void showEventDialog(LocalDate date, EventRecord selectedEvent) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Event Details");
        dialog.setHeaderText("Event Scheduled for " + date);


        // Create initial date picker
        DatePicker initialDatePicker = new DatePicker(date);
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
        //initialDatePicker.getEditor().setTextFormatter(new TextFormatter<>(stringConverter));
        initialDatePicker.getEditor().focusedProperty().addListener(buildFocusListener(initialDatePicker));

        // Create end date picker
        DatePicker endDatePicker = new DatePicker(date.plusDays(1l));
        endDatePicker.setConverter(stringConverter);
        endDatePicker.getEditor().focusedProperty().addListener(buildFocusListener(endDatePicker));

        // Create description text field
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Event Description");

        // Set up the button types
        ButtonType createEventType = new ButtonType("Create Event");

        dialog.getDialogPane().getButtonTypes().addAll(createEventType, ButtonType.CANCEL);

        // Create list view for displaying events
        ListView<EventRecord> eventListView = new ListView<>();
        eventListView.setPrefHeight(100);
        //eventListView.getItems().addAll(getEventsForDate(date));
        fetchData(eventListView, date);

        // Set cell factory for the list view to include "Edit" and "Delete" links in each row
        eventListView.setCellFactory(param -> new ListCell<EventRecord>() {
            private final Hyperlink editLink = new Hyperlink("Edit");
            private final Hyperlink deleteLink = new Hyperlink("Delete");

            {
                editLink.setOnAction(event -> {
                    EventRecord selectedItem = getItem();
                    if (selectedItem != null) {
                        initialDatePicker.setValue(selectedItem.getStartDate());
                        endDatePicker.setValue(selectedItem.getEndDate());
                        descriptionField.setText(selectedItem.getDescription());
                    }
                });

                deleteLink.setOnAction(event -> {
                    EventRecord selectedItem = getItem();
                    if (selectedItem != null) {
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
                    setGraphic(new HBox(5, editLink, deleteLink));
                }
            }
        });

        // Populate fields if editing an existing event
        if (selectedEvent != null) {
            initialDatePicker.setValue(selectedEvent.getStartDate());
            endDatePicker.setValue(selectedEvent.getEndDate());
            descriptionField.setText(selectedEvent.getDescription());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        grid.add(new Label("Events for " + date), 0, 0);
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
        //initialDatePicker.getEditor().setDisable(true);

        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->
                createEventButton.setDisable(newValue == null || initialDatePicker.getValue() == null || descriptionField.getText().isEmpty()));
        endDatePicker.getEditor().setDisable(true);
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
                initialDatePicker.getEditor().commitValue();
                LocalDate initialDate = initialDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                String description = descriptionField.getText();
                createEvent(initialDate, endDate, description, eventListView);
                // Perform action with event details (e.g., add/update event to a list)
                System.out.println("Initial Date: " + initialDate + "::::" + initialDatePicker.getEditor().getTextFormatter());
                System.out.println("End Date: " + endDate);
                System.out.println("Description: " + description);
            }
        });
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

    private LocalDate parseLocalDate(String str) {
        try {
            return LocalDate.parse(str);
        } catch (Exception e) {
            // Return null if parsing fails
            return null;
        }
    }

    private void createEvent(LocalDate initialDate, LocalDate endDate, String description, ListView<EventRecord> eventListView) {
        System.out.println("Create event" + initialDate + endDate);
        LOGGER.log(Level.INFO, "Fetching data for current month {0}", currentYearMonth);
        CreateEventApiService eventAPIService = new CreateEventApiService("111", new EventRequest(initialDate, endDate, description, "abc1"));
        eventAPIService.setOnSucceeded(event -> {
            EventRecord eventRecord = eventAPIService.getValue();
            processEventCreated(eventRecord);
        });
        eventAPIService.setOnFailed(event -> {
            LOGGER.log(Level.SEVERE, eventAPIService.getException(), () -> "Failed to save event data: " + eventAPIService.getException().getMessage());
            displayExceptionAlert(Alert.AlertType.ERROR, "Exception has occurred", eventAPIService.getException().getMessage());
        });
        eventAPIService.start();
        LOGGER.log(Level.INFO, "Saving event has started");
    }

    private void fetchData(ListView<EventRecord> eventListView, LocalDate date) {
        List<EventRecord> eventsOfDay = getEventMap(this.events).getOrDefault(date, List.of());
        LOGGER.log(Level.INFO, "Fetching data for day selected", eventsOfDay);
        eventListView.getItems().addAll(eventsOfDay);
    }

    private void displayExceptionAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private List<EventRecord> getEventsForDate(LocalDate date) {
        // Retrieve events for the specified date (replace with actual data retrieval logic)
        return Arrays.asList(
                new EventRecord(date, date.plusDays(1), "Event 1"),
                new EventRecord(date.plusDays(2), date.plusDays(3), "Event 2"),
                new EventRecord(date.plusDays(4), date.plusDays(5), "Event 3")
        );
    }


    private void addHeaderRow() {
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label label = new Label(daysOfWeek[i]);
            label.setTextFill(Color.WHITE);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            calendarGrid.add(label, i, 0);
            GridPane.setHalignment(label, HPos.CENTER);
        }
    }

}
