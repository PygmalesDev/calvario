package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Announcement;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Resource;
import static de.uniks.stp24.service.Constants.*;

import de.uniks.stp24.service.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Objects;
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
    @Inject
    @Named("technologiesResourceBundle")
    public ResourceBundle technologiesResourceBundle;

    ObservableList<Announcement> announcements = FXCollections.observableArrayList();

    @Inject
    public AnnouncementsService() {

    }

    public void addAnnouncement(Jobs.Job job) {
        if (job.progress() >= job.total()) {
            String message = "";
            Island island = islandsService.getIsland(job.system());
            String islandName = "";
            if (Objects.nonNull(island)) {
                islandName = island.name().isEmpty() ? gameResourceBundle.getString("uncharted") : island.name();
            }
            ArrayList<Consumer<Jobs.Job>> forwardMethods = new ArrayList<>();
            String forwardIcon = "";

            switch (job.type()) {
                case "upgrade" -> {
                    message = gameResourceBundle.getString("captain.upgrade.ready")
                            .replace("{upgradeLevel}", gameResourceBundle.getString(upgradeTranslation.get(island.upgrade())))
                            .replace("{islandName}", islandName);
                    forwardMethods.add(jobsService.getJobInspector("island_upgrade"));
                    forwardIcon = "-fx-background-image: url('[PATH]')"
                            .replace("[PATH]", "/icons/islands/" + island.type() + ".png");
                }
                case "district" -> {
                    message = gameResourceBundle.getString("captain.site.ready")
                            .replace("{siteId}", this.gameResourceBundle.getString(
                                    Constants.siteTranslation.get(job.district())) + " Site")
                            .replace("{islandName}", islandName);
                    forwardMethods.add(jobsService.getJobInspector("island_jobs_overview"));
                    forwardMethods.add(jobsService.getJobInspector("site_overview"));
                    forwardIcon = "-fx-background-image: url('[PATH]')"
                            .replace("[PATH]", "/" + sitesIconPathsMap.get(job.district()));
                }
                case "building" -> {
                    message = gameResourceBundle.getString("captain.building.ready")
                            .replace("{buildingId}", this.gameResourceBundle.getString(
                                    Constants.buildingTranslation.get(job.building())))
                            .replace("{islandName}", islandName);
                    forwardMethods.add(jobsService.getJobInspector("island_jobs_overview"));
                    forwardMethods.add(jobsService.getJobInspector("building_done_overview"));
                    forwardIcon = "-fx-background-image: url('[PATH]')"
                            .replace("[PATH]", "/" + buildingsIconPathsMap.get(job.building()));
                }
                case "technology" -> {
                    message = gameResourceBundle.getString("captain.technology.ready")
                            .replace("{technologyId}", technologiesResourceBundle.getString(job.technology()));
                    forwardMethods.add(jobsService.getJobInspector("island_jobs_overview"));
                    forwardIcon = "-fx-background-image: url('[PATH]')"
                            .replace("[PATH]", "/" + technologyIconMap.get(job.technology()));
                }
            }
            announcements.addFirst(new Announcement(message, forwardIcon, forwardMethods, job));
        }
    }

    public void addAnnouncement(Resource resource) {
        String message = gameResourceBundle.getString("captain.debt")
                .replace("{resourceCount}", String.valueOf(resource.count()))
                .replace("{resourceId}", gameResourceBundle.getString(resourceTranslation.get(resource.resourceID())));
        ArrayList<Consumer<Jobs.Job>> forwardMethods = new ArrayList<>();
        forwardMethods.add(jobsService.getJobInspector("storage_overview"));
        String forwardIcon = resourceImagePath.get(resource.resourceID());
        announcements.add(new Announcement(message, forwardIcon, forwardMethods, null));
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
