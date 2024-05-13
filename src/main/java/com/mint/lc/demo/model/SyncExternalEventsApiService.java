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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncExternalEventsApiService extends Service<List<EventRecord>> {

    private static final Logger LOGGER = Logger.getLogger(SyncExternalEventsApiService.class.getName());

    private static final String DEFAULT_URL = "/instructors/%s/events/externals";
    private final String apiUrl;
    private final YearMonth month;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .setPrettyPrinting().create();

    public SyncExternalEventsApiService(String instructorId, YearMonth month) {
        this.apiUrl = System.getProperty(Contractor.Model.API_URL_PROPERTY) +
                String.format(DEFAULT_URL, instructorId);
        this.month = month;
    }

    @Override
    protected Task<List<EventRecord>> createTask() {
        return new Task<>() {
            @Override
            protected List<EventRecord> call() throws Exception {
                LOGGER.log(Level.INFO, "Calling create events api {0}", apiUrl);
                String monthParam = month.format(DateTimeFormatter.ofPattern(Contractor.Model.MONTH_REQUEST_FORMAT));
                LOGGER.log(Level.INFO, "Calling sync events api with request {0}", monthParam);
                HttpResponse<JsonNode> jsonResponse = Unirest.post(apiUrl)
                        .header("accept", "application/json")
                        .header("content-type", "application/json")
                        .queryString("month", monthParam)
                        .asJson();

                String body = jsonResponse.getBody().toString();
                LOGGER.log(Level.INFO, "Processing answer: {0}", body);
                EventRecord[] eventRecordsArray = gson.fromJson(body, EventRecord[].class);
                return List.of(eventRecordsArray);
            }
        };
    }
}
