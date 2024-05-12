package com.mint.lc.demo.view;

import com.mint.lc.demo.LoginContractor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Objects;
import java.util.logging.Logger;

public class LoginScreen implements LoginContractor.View {

    private static final Logger LOGGER = Logger.getLogger(LoginScreen.class.getName());

    private final LoginContractor.Presenter presenter;
    private Label validationLabel;

    public LoginScreen(LoginContractor.Presenter loginPresenter) {
        this.presenter = loginPresenter;
    }

    public Scene build() {
        LOGGER.info("Building login screen");
        Image logoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/app_logo.jpg")));
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(150);
        logoImageView.setPreserveRatio(true);

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

        validationLabel = new Label();
        validationLabel.setTextFill(Color.RED);
        validationLabel.setFont(Font.font("Arial", 10));

        Button loginButton = new Button("Login");
        loginButton.setId("login-button");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            presenter.doLogin(username, password);
        });

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

        VBox root = new VBox(20);
        root.getChildren().addAll(logoImageView, gridPane);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setBackground(BACKGROUND);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());
        return scene;
    }

    public void showError(String message) {
        this.validationLabel.setText(message);
        this.validationLabel.setTextFill(Color.RED);
    }

}
