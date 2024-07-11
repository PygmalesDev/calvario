package de.uniks.stp24.model;

import javafx.scene.image.Image;

import java.util.function.Consumer;

public record Announcement(
        String message,
        boolean showForward,
        Image forwardIcon,
        Consumer<String[]> forwardMethod,
        Jobs.Job job) {
}
