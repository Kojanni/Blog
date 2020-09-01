package com.kochetkova.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class LocalDateTimeSerializer  extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        try {
            ZonedDateTime zoneDateTime = localDateTime.atZone(ZoneOffset.UTC);
            long seconds = zoneDateTime.toInstant().toEpochMilli()/1000;
            String timestamp = "" + seconds;

            gen.writeString(timestamp);
        } catch (DateTimeParseException e) {
            System.err.println(e);

            gen.writeString("");
        }
    }
}
