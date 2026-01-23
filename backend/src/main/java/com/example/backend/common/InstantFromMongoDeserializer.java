package com.example.backend.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.Instant;

public class InstantFromMongoDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (node.isTextual()) {
            String text = node.asText();
            if (text.matches("\\d+")) {
                return Instant.ofEpochMilli(Long.parseLong(text));
            }
            return Instant.parse(text);
        }
        if (node.isNumber()) return Instant.ofEpochMilli(node.asLong());
        if (node.isObject() && node.has("$date")) {
            JsonNode dateNode = node.get("$date");
            if (dateNode.isNumber()) {
                return Instant.ofEpochMilli(dateNode.asLong());
            }
            return Instant.parse(dateNode.asText());
        }
        throw new IllegalArgumentException("Unsupported date format: " + node);
    }
}