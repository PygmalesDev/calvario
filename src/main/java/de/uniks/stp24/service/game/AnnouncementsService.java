package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Announcement;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Resource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class AnnouncementsService {
    @Inject
    JobsService jobsService;
    @Inject
    IslandsService islandsService;
    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    ObservableList<Announcement> announcements = FXCollections.observableArrayList();

    @Inject
    public AnnouncementsService() {

    }

    public AnnouncementsService addAnnouncement(Jobs.Job job) {
        // todo change text
        String message = "";
        Island island = islandsService.getIsland(job.system());
        Consumer<String[]> forwardMethod = null;

        switch (job.type()) {
            case "upgrade" -> {
                message = "Yoooho " + island.name() + " is a " + island.upgrade() + " now, aye!";
                forwardMethod = jobsService.getJobInspector("island_jobs_overview");
            }
            case "district"  -> {
                message = "Construction of " + job.district() + " on " + island.name() + " is finished!";
                forwardMethod = jobsService.getJobInspector("island_jobs_overview");
            }
            case "building" -> {
                message = "Construction of " +  job.building() + " on " + island.name() + " is finished!";
                forwardMethod = jobsService.getJobInspector("island_jobs_overview");
            }
            case "technology" -> {
                message = "Your scout finished " + job.technology();
//                forwardMethod = jobsService.getJobInspector("island_jobs_overview");
            }
        }
        announcements.addFirst(new Announcement(message, true, null, forwardMethod, job));
        return this;
    }

    public AnnouncementsService addAnnouncement(Resource resource) {
        // todo change text
        String message =  "Bruh, you are broke. You have only " + resource.count() + " " +
                gameResourceBundle.getString(resource.resourceID()) + "! Get your act togehther!";
        announcements.add(new Announcement(message, false, null, null, null));
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
