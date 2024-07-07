package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Announcement;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Resource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

public class AnnouncementsService {
    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    ObservableList<Object> announcements = FXCollections.observableArrayList();

    @Inject
    public AnnouncementsService() {

    }

    public AnnouncementsService addAnnouncement(Jobs.Job job) {
        announcements.add(job);
        return this;
    }

    public AnnouncementsService addAnnouncement(Resource resource) {
        announcements.add(resource);
        return this;
    }

    public Announcement getNextAnnouncement() {
        Object announcement = announcements.removeFirst();
        String message;
        boolean showForward;
        Runnable forwardMethod;
        if (announcement instanceof Resource resource) {
            // todo change text
            message =  "Bruh, you are broke. You have only " + resource.count() + " " +
                    gameResourceBundle.getString(resource.resourceID()) + "! Get your act togehther!";
            showForward = false;
            forwardMethod = null;
        } else {
            // Todo for jobs
            message =  "";
            showForward = true;
            forwardMethod = null;
        }
        return new Announcement(message, showForward, forwardMethod);
    }

    public ObservableList<Object> getAnnouncements() {
        return announcements;
    }
}
