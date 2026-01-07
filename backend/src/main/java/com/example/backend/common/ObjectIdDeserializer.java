package com.example.backend.common;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Map;

public class ObjectIdDeserializer extends JsonDeserializer {

    @Override
    public String deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JacksonException {
        if (p.currentToken().isStructStart()) {
            JsonNode node = p.getCodec().readTree(p);
            if (node.has("$oid")) {
                return node.get("$oid").asText();
            }
        }
        return p.getValueAsString();
    }
}
