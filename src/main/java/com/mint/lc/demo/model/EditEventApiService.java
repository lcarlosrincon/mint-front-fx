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

public class EditEventApiService extends Service<EventRecord> {

    private static final Logger LOGGER = Logger.getLogger( EditEventApiService.class.getName() );

    private static final String DEFAULT_URL = "/instructors/%s/events/%s";
    private final String apiUrl;
    private final EventRecord request;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .setPrettyPrinting().create();

    public EditEventApiService(String instructorId, EventRecord request) {
        this.apiUrl = System.getProperty(Contractor.Model.API_URL_PROPERTY) + String.format(DEFAULT_URL, instructorId, request.getId());
        this.request = request;
    }

    @Override
    protected Task<EventRecord> createTask() {
        return new Task<>() {
            @Override
            protected EventRecord call() throws Exception {
                LOGGER.log(Level.INFO, "Calling edit events api {0}", apiUrl);
                var jsonRequest = gson.toJson(request);
                LOGGER.log(Level.INFO, "Calling edit events api with request {0}", jsonRequest);
                HttpResponse<JsonNode> jsonResponse = Unirest.put(apiUrl)
                        .header("accept", "application/json")
                        .header("content-type", "application/json")
                        .body(jsonRequest)
                        .asJson();

                String body = jsonResponse.getBody().toString();
                LOGGER.log(Level.INFO, "Processing answer: {0}", body);
                return gson.fromJson(body, EventRecord.class);
            }
        };
    }
}
