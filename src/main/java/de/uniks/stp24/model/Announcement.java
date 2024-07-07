package de.uniks.stp24.model;

public record Announcement(
        String message,
        Boolean showForward,
        Runnable forwardMethod) {
}
