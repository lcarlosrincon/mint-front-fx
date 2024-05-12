package com.mint.lc.demo.presenter;

import com.mint.lc.demo.model.CalendarModel;
import com.mint.lc.demo.model.GetEventsApiService;
import com.mint.lc.demo.model.dto.EventRecord;
import com.mint.lc.demo.model.dto.Instructor;
import com.mint.lc.demo.view.CalendarScreen;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CalendarPresenter {

    private static final Logger LOGGER = Logger.getLogger(CalendarPresenter.class.getName());

    private final CalendarScreen screen;

    private final CalendarModel model;

    public CalendarPresenter(Instructor instructor) {
        this.model = new CalendarModel(instructor, YearMonth.now(), List.of());
        this.screen = new CalendarScreen(this);
    }

    public void start(Stage stage) {
        Scene scene = screen.build();
        stage.setScene(scene);
        stage.setTitle(this.model.getInstructor().getFirstName() + " Calendar");
        stage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/company_logo.png"))));
        stage.show();
        this.updateCalendar();
    }

    public void goNextMonth() {
        this.model.setCurrentYearMonth(getCurrentYearMonth().plusMonths(1));
        this.screen.updateMonthLabel();
        this.updateCalendar();
    }

    public void goPreviousMonth() {
        this.model.setCurrentYearMonth(getCurrentYearMonth().minusMonths(1));
        this.screen.updateMonthLabel();
        this.updateCalendar();
    }

    public void updateCalendar() {
        // paint empty calendar
        this.model.setEvents(List.of());
        this.screen.updateGridCalendar();
        GetEventsApiService eventAPIService = new GetEventsApiService(this.model.getInstructor().getId(), getCurrentYearMonth());
        eventAPIService.setOnSucceeded(event -> {
            List<EventRecord> eventRecords = eventAPIService.getValue();
            if (eventRecords.isEmpty()) {
                this.screen.displayExceptionAlert(Alert.AlertType.INFORMATION, "Create Events", "You don't have events created");
            } else {
                processListEvents(eventRecords);
            }
        });
        eventAPIService.setOnFailed(event -> {
            LOGGER.log(Level.SEVERE, "Failed to fetch event data: " + eventAPIService.getException().getMessage());
            this.screen.displayExceptionAlert(Alert.AlertType.ERROR, "Exception has occurred", eventAPIService.getException().getMessage());
        });
        eventAPIService.start();
    }

    private void processListEvents(List<EventRecord> eventRecords) {
        this.model.setEvents(eventRecords);
        this.screen.updateGridCalendar();
    }

    public void showEventDialog(LocalDate selectedDate) {
        List<EventRecord> eventsOfDay = getEventMap().getOrDefault(selectedDate, List.of());
        EventPresenter eventPresenter = new EventPresenter(eventsOfDay, selectedDate, model.getInstructor());
        eventPresenter.start(newEvent -> processEventCreated(newEvent));
    }

    private void processEventCreated(EventRecord eventRecord) {
        this.model.setEvents(Stream.concat(this.getEvents().stream(),
                Stream.of(eventRecord)).collect(Collectors.toList()));
        this.screen.updateGridCalendar();
    }

    public YearMonth getCurrentYearMonth() {
        return this.model.getCurrentYearMonth();
    }

    public List<EventRecord> getEvents() {
        return this.model.getEvents();
    }

    public Map<LocalDate, List<EventRecord>> getEventMap() {
        return IntStream.rangeClosed(1, getCurrentYearMonth().lengthOfMonth()).boxed()
                .collect(Collectors.toMap(day -> getCurrentYearMonth().atDay(day),
                        day -> getEvents(getCurrentYearMonth().atDay(day), this.getEvents()),
                        (d1, d2) -> d1));
    }

    private List<EventRecord> getEvents(LocalDate atDay, List<EventRecord> events) {
        return events.stream().filter(event -> event.isThisDay(atDay)).collect(Collectors.toList());
    }
}
