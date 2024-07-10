package de.uniks.stp24.model;

import javafx.scene.image.Image;

public record Announcement(
        String message,
        boolean showForward,
        Image forwardIcon,
        Runnable forwardMethod) {
}
