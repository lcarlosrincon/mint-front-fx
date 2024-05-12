package com.mint.lc.demo.presenter;

import com.mint.lc.demo.LoginContractor;
import com.mint.lc.demo.model.dto.Instructor;
import com.mint.lc.demo.model.LoginApiService;
import com.mint.lc.demo.view.CalendarScreen;
import com.mint.lc.demo.view.LoginScreen;
import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginPresenter extends Application implements LoginContractor.Presenter {

    private static final Logger LOGGER = Logger.getLogger(LoginPresenter.class.getName());

    private LoginContractor.View screen;
    private LoginContractor.Model<WorkerStateEvent> model;

    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.screen = new LoginScreen(this);
        this.stage = primaryStage;
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/company_logo.png"))));
        primaryStage.setTitle("Mint Calendar");
        primaryStage.setScene(this.screen.build());
        primaryStage.show();
    }

    public void doLogin(String username, String password) {
        if (!username.isEmpty() && !password.isEmpty()) {
            this.model = new LoginApiService();
            this.model.setOnSucceeded(event -> {
                Instructor instructor = this.model.getValue();
                openCalendarScreen(instructor);
            });
            this.model.setOnFailed(event -> {
                LOGGER.log(Level.SEVERE, "Failed to fetch data: " + this.model.getException().getMessage());
                this.screen.showError("Invalid username or password");
            });
            this.model.execute(username, password);
        } else {
            this.screen.showError("Ingress user and password");
        }
    }

    private void openCalendarScreen(Instructor instructor) {
        stage.close();

        CalendarScreen mainApp = new CalendarScreen(instructor);
        mainApp.start(new Stage());
    }

}
