package com.mint.lc.demo;

import com.mint.lc.demo.model.dto.EventRecord;
import com.mint.lc.demo.model.dto.Instructor;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface CalendarContractor {

    interface View extends Contractor.View {

        void displayExceptionAlert(Alert.AlertType alertType, String title, String message);

        void updateGridCalendar();

        void updateMonthLabel();
    }

    interface Presenter {
        void start(Stage stage);

        void goNextMonth();

        void goPreviousMonth();

        void showEventDialog(LocalDate selectedDate);

        YearMonth getCurrentYearMonth();

        Map<LocalDate, List<EventRecord>> getEventMap();

        void syncExternalCalendars();
    }

    interface Model {
        Instructor getInstructor();

        void setInstructor(Instructor instructor);

        YearMonth getCurrentYearMonth();

        void setCurrentYearMonth(YearMonth currentYearMonth);

        List<EventRecord> getEvents();

        void setEvents(List<EventRecord> events);

    }
}
