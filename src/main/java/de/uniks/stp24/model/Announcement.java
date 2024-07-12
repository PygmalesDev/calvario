package de.uniks.stp24.model;

import java.util.ArrayList;
import java.util.function.Consumer;

public record Announcement(
        String message,
        boolean showForward,
        String forwardIcon,
        ArrayList<Consumer<Jobs.Job>> forwardMethod,
        Jobs.Job job) {
}
