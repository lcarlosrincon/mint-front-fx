package com.mint.lc.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginApiService extends Service<Instructor> {

    private static final Logger LOGGER = Logger.getLogger(LoginApiService.class.getName());

    private static final String DEFAULT_URL = "http://localhost:8081/v1/login";
    private final String apiUrl;
    private final String username;
    private final String password;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .setPrettyPrinting().create();

    public LoginApiService(String username, String password) {
        this.apiUrl = DEFAULT_URL;
        this.username = username;
        this.password = password;
    }

    @Override
    protected Task<Instructor> createTask() {
        return new Task<>() {
            @Override
            protected Instructor call() throws Exception {
                LOGGER.log(Level.INFO, "Calling login api {0}", apiUrl);
                var jsonRequest = gson.toJson(new LogRequest(username, password));
                LOGGER.log(Level.INFO, "Calling login api with request {0}", jsonRequest);
                HttpResponse<JsonNode> jsonResponse = Unirest.post(apiUrl)
                        .header("accept", "application/json")
                        .header("content-type", "application/json")
                        .body(jsonRequest)
                        .asJson();

                String body = jsonResponse.getBody().toString();
                LOGGER.log(Level.INFO, "Processing answer: {0}", body);
                return gson.fromJson(body, Instructor.class);
            }
        };
    }
}
