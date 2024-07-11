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

    ObservableList<Announcement> announcements = FXCollections.observableArrayList();

    @Inject
    public AnnouncementsService() {

    }

    public AnnouncementsService addAnnouncement(Jobs.Job job) {
        // todo change
        String message = "Building " + job.building() + " on " + job.system() + " is done! good job!";
        boolean showForward = true;
        Runnable forwardMethod = null;
        announcements.addFirst(new Announcement(message, showForward, null, forwardMethod));
        return this;
    }

    public AnnouncementsService addAnnouncement(Resource resource) {
        // todo change text
        String message =  "Bruh, you are broke. You have only " + resource.count() + " " +
                gameResourceBundle.getString(resource.resourceID()) + "! Get your act togehther!";
        boolean showForward = false;
        Runnable forwardMethod = null;
        announcements.add(new Announcement(message, showForward, null, forwardMethod));
        return this;
    }

    public Announcement getNextAnnouncement() {
        return announcements.removeFirst();
    }

    public ObservableList<Announcement> getAnnouncements() {
        return announcements;
    }

    public void clearAnnouncements() {
        announcements.clear();
    }
}
