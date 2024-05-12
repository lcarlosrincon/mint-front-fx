module com.mint.lc.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires unirest.java;
    requires java.logging;

    opens com.mint.lc.demo to javafx.fxml, com.google.gson;
    //opens java.time to com.google.gson;
    exports com.mint.lc.demo;
}