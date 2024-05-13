package com.mint.lc.demo.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mint.lc.demo.Contractor;
import com.mint.lc.demo.model.dto.EventRecord;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteEventApiService extends Service<EventRecord> {

    private static final Logger LOGGER = Logger.getLogger(DeleteEventApiService.class.getName());

    private static final String DEFAULT_URL = "/instructors/%s/events/%s";
    private final String apiUrl;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .setPrettyPrinting().create();

    public DeleteEventApiService(String instructorId, String eventId) {
        this.apiUrl = System.getProperty(Contractor.Model.API_URL_PROPERTY) +
                String.format(DEFAULT_URL, instructorId, eventId);
    }

    @Override
    protected Task<EventRecord> createTask() {
        return new Task<>() {
            @Override
            protected EventRecord call() throws Exception {
                LOGGER.log(Level.INFO, "Calling delete events api {0}", apiUrl);
                HttpResponse<JsonNode> jsonResponse = Unirest.delete(apiUrl)
                        .header("accept", "application/json")
                        .asJson();

                String body = jsonResponse.getBody().toString();
                LOGGER.log(Level.INFO, "Processing answer: {0}", body);
                return gson.fromJson(body, EventRecord.class);
            }
        };
    }
}
