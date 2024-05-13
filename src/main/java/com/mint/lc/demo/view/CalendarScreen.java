package com.mint.lc.demo.view;

import com.mint.lc.demo.CalendarContractor;
import com.mint.lc.demo.model.dto.EventRecord;
import com.mint.lc.demo.presenter.CalendarPresenter;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class CalendarScreen implements CalendarContractor.View {

    private static final Logger LOGGER = Logger.getLogger(CalendarScreen.class.getName());

    private final CalendarContractor.Presenter presenter;

    private GridPane calendarGrid;
    private Label monthLabel;

    public CalendarScreen(CalendarPresenter presenter) {
        this.presenter = presenter;
    }

    public Scene build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setBackground(LoginScreen.BACKGROUND);

        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(0, 0, 10, 0));

        monthLabel = new Label();
        monthLabel.setTextFill(Color.WHITE);
        updateMonthLabel();

        HBox navigationBox = new HBox(10);
        navigationBox.setAlignment(Pos.CENTER);

        Button previousMonthButton = new Button("<< Previous Month");
        previousMonthButton.setOnAction(event -> this.presenter.goPreviousMonth());

        Button nextMonthButton = new Button("Next Month >>");
        nextMonthButton.setOnAction(event -> this.presenter.goNextMonth());

        navigationBox.getChildren().addAll(previousMonthButton, nextMonthButton);
        topBox.getChildren().addAll(monthLabel, navigationBox);
        root.setTop(topBox);

        calendarGrid = new GridPane();
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setAlignment(Pos.CENTER);

        addHeaderRow();

        root.setCenter(calendarGrid);

        return new Scene(root, 600, 400);
    }

    public void updateMonthLabel() {
        String monthText = this.presenter.getCurrentYearMonth().getMonth().toString() + " " + this.presenter.getCurrentYearMonth().getYear();
        monthLabel.setText(monthText);
    }

    public void updateGridCalendar() {
        calendarGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) > 0); // Remove existing calendar rows

        int daysInMonth = this.presenter.getCurrentYearMonth().lengthOfMonth();
        int firstDayOfWeek = this.presenter.getCurrentYearMonth().atDay(1).getDayOfWeek().getValue();
        Map<LocalDate, List<EventRecord>> eventsMap = this.presenter.getEventMap();
        LOGGER.info("Events to paint " + eventsMap);

        for (int i = 1; i <= daysInMonth; i++) {
            Button dayButton = new Button(Integer.toString(i));
            dayButton.setPrefWidth(50);
            dayButton.setPrefHeight(50);

            LocalDate currentDate = this.presenter.getCurrentYearMonth().atDay(i);
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

            dayButton.setOnAction(event -> this.presenter.showEventDialog(currentDate));
        }
    }

    private Color getColorForEventCount(int numEvents) {
        int color = Math.min(numEvents * 10, 100) * 255 / 100;
        Random random = new Random(color);
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }


    public void displayExceptionAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
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
