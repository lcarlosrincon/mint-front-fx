package com.mint.lc.demo.model;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateSerializer implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd").withLocale(Locale.ENGLISH);

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDate.parse(json.getAsString(), FORMATTER);
    }

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(FORMATTER.format(localDate));
    }
}
