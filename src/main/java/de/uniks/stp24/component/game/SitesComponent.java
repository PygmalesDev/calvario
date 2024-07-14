package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs.*;
import de.uniks.stp24.model.SiteProperties;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.JobsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.*;

@Component(view = "Sites.fxml")
public class SitesComponent extends VBox {

    @FXML
    public ListView<SiteProperties> sitesListView;
    ObservableList<SiteProperties> sitePropertiesList = FXCollections.observableArrayList();

    @Inject
    public Provider<DistrictComponent> districtComponentProvider;

    @Inject
    App app;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    JobsService jobsService;

    @Inject
    IslandAttributeStorage attributeStorage;

    InGameController inGameController;

    @Inject
    public SitesComponent() {

    }

    @OnRender
    public void render() {
        this.sitesListView.setCellFactory(list ->
                new ComponentListCell<>(this.app, this.districtComponentProvider));
    }

    public void setSitesBox(Island island) {
        this.sitePropertiesList.clear();
        List<Job> siteJobs = this.jobsService.getObservableListForSystem(island.id()).stream()
                .filter(job -> job.type().equals("district")).toList();

        island.sites().forEach((site, capacity) -> this.sitePropertiesList.add(new SiteProperties(
                this.inGameController,
                site,
                capacity + "/" + island.sitesSlots().get(site),
                siteJobs.stream()
                        .filter(job -> job.district().equals(site))
                        .findFirst().orElse(null)
        )));
        this.sitesListView.setItems(this.sitePropertiesList);
    }

    @OnRender
    public void setSiteJobsUpdates() {
        this.jobsService.onJobCommonUpdates(() -> this.setSitesBox(this.attributeStorage.island));
    }


    public int getTotalSiteSlots(Island island){
        int totalSiteSlots = 0;
        for (Map.Entry<String, Integer> entry : island.sites().entrySet()) {
            totalSiteSlots = totalSiteSlots + entry.getValue();
        }
        return totalSiteSlots;
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }
}