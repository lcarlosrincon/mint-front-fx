module com.mint.lc.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires unirest.java;
    requires java.logging;

    opens com.mint.lc.demo to javafx.fxml, com.google.gson;
    //opens java.time to com.google.gson;
    exports com.mint.lc.demo;
    exports com.mint.lc.demo.view;
    exports com.mint.lc.demo.presenter;
    opens com.mint.lc.demo.presenter to javafx.graphics;
    opens com.mint.lc.demo.view to com.google.gson, javafx.fxml;
    exports com.mint.lc.demo.model;
    opens com.mint.lc.demo.model to com.google.gson, javafx.fxml;
    exports com.mint.lc.demo.model.dto;
    opens com.mint.lc.demo.model.dto to com.google.gson, javafx.fxml;
}