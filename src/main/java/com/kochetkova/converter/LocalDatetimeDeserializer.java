package com.kochetkova.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;


public class LocalDatetimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
        long seconds = Long.parseLong(p.getText());
        Instant instant = Instant.ofEpochSecond(seconds);

        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            System.err.println(e);

            return null;
        }
    }
}