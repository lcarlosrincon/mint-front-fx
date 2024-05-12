package com.mint.lc.demo;

import com.mint.lc.demo.model.dto.Instructor;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public interface LoginContractor {

    interface View extends Contractor.View {
        void showError(String message);
    }

    interface Presenter {
        void start(Stage stage);

        void doLogin(String username, String password);
    }

    interface Model<T extends Event> extends Contractor.Model<Instructor> {
        void execute(String username, String password);

        void setOnSucceeded(EventHandler<T> var1);

        void setOnFailed(EventHandler<T> var1);

    }
}
