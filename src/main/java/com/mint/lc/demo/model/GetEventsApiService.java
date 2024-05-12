package com.mint.lc.demo.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mint.lc.demo.model.dto.EventRecord;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetEventsApiService extends Service<List<EventRecord>> {

    private static final Logger LOGGER = Logger.getLogger( GetEventsApiService.class.getName() );

    private static final String DEFAULT_URL = "http://localhost:8081/instructors/%s/events?month=%s";
    public static final String MONTH_REQUEST_FORMAT = "yyyy-MM";
    private final String apiUrl;

    public GetEventsApiService(String instructorId, YearMonth month) {
        this.apiUrl = String.format(DEFAULT_URL, instructorId,
                month.format(DateTimeFormatter.ofPattern(MONTH_REQUEST_FORMAT)));
    }

    @Override
    protected Task<List<EventRecord>> createTask() {
        return new Task<>() {
            @Override
            protected List<EventRecord> call() throws Exception {
                LOGGER.log(Level.INFO, "Calling get events api {0}", apiUrl);
                HttpResponse<JsonNode> jsonResponse = Unirest.get(apiUrl)
                        .header("accept", "application/json")
                        .asJson();

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                        .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                        .setPrettyPrinting().create();
                String body = jsonResponse.getBody().toString();
                LOGGER.log(Level.INFO, "Processing answer: {0}", body);
                EventRecord[] eventRecordsArray = gson.fromJson(body, EventRecord[].class);
                LOGGER.log(Level.INFO, "{0} Events: {1}", new Object[]{eventRecordsArray.length, eventRecordsArray});
                return List.of(eventRecordsArray);
            }
        };
    }
}
