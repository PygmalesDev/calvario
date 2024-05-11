package de.uniks.stp24.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ColorDeserializer extends StdDeserializer<Color> {

    public ColorDeserializer() {
        super(Color.class);
    }

    @Override
    public Color deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        double red = node.get("red").asDouble();
        double green = node.get("green").asDouble();
        double blue = node.get("blue").asDouble();
        double opacity = node.get("opacity").asDouble();
        Color color = new Color(red, green, blue, opacity);
        return color;
    }
}
