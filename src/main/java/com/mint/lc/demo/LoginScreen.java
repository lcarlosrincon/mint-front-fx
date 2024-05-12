package com.mint.lc.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginScreen extends Application {

    private static final Logger LOGGER = Logger.getLogger(LoginScreen.class.getName());

    public static final Background BACKGROUND = new Background(new BackgroundFill(
            Color.BLACK,
            new CornerRadii(0),
            new Insets(0)
    ));
    public static final Font DEFAULT_FONT = Font.font("Arial", 14);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/company_logo.png")));
        // Create the company logo
        Image logoImage = new Image(getClass().getResourceAsStream("/app_logo.jpg"));
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(150);
        logoImageView.setPreserveRatio(true);


        // Create the username and password fields with labels
        Label usernameLabel = new Label("Instructor:");
        usernameLabel.setTextFill(Color.WHITE);
        usernameLabel.setFont(DEFAULT_FONT);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your id");

        Label passwordLabel = new Label("Password:");
        passwordLabel.setTextFill(Color.WHITE);
        passwordLabel.setFont(DEFAULT_FONT);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");


        // Create validation labels
        Label validationLabel = new Label();
        validationLabel.setTextFill(Color.RED);
        validationLabel.setFont(Font.font("Arial", 10));

        // Create the login button
        Button loginButton = new Button("Login");
        loginButton.setId("login-button"); // Set an ID for the button
        loginButton.setOnAction(e -> {
            // Check username and password
            String username = usernameField.getText();
            String password = passwordField.getText();
            doValidLogin(primaryStage, validationLabel, username, password);
        });

        // Create GridPane for labels and fields
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginButton, 1, 2);
        gridPane.add(validationLabel, 1, 3);

        // Create VBox to contain logo and gridPane
        VBox root = new VBox(20);
        root.getChildren().addAll(logoImageView, gridPane);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        // Create a black background
        root.setBackground(BACKGROUND);

        // Set up the scene
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void doValidLogin(Stage primary, Label validationLabel, String username, String password) {
        if (!username.isEmpty() && !password.isEmpty()) {
            LoginApiService service = new LoginApiService(username, password);
            service.setOnSucceeded(event -> {
                Instructor instructor = service.getValue();
                openMainApplication(primary);
            });
            service.setOnFailed(event -> {
                LOGGER.log(Level.SEVERE, "Failed to fetch data: " + service.getException().getMessage());
                validationLabel.setText("Invalid username or password");
            });
            service.start();
        } else {
            validationLabel.setText("Ingress user and password");
            validationLabel.setTextFill(Color.RED);
        }
    }

    private void openMainApplication(Stage primaryStage) {
        // Close the current login screen
        primaryStage.close();

        // Open the main application screen
        // Replace this with the code to open your main application screen
        EventSchedulerApp mainApp = new EventSchedulerApp();
        mainApp.start(new Stage());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
