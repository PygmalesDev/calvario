package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Jobs.*;
import de.uniks.stp24.model.SiteProperties;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;

import static de.uniks.stp24.service.Constants.sitesIconPathsMap;

@Component(view = "District.fxml")
public class DistrictComponent extends VBox implements ReusableItemComponent<SiteProperties> {
    @FXML
    public Text districtCapacity;
    @FXML
    Button siteElementButton;
    @FXML
    HBox jobProgressBox;
    @FXML
    Text jobTimeText;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    IslandAttributeStorage islandAttributeStorage;

    Map<String, String> sitesMap;
    int jobProgress;
    int totalProgress;

    private InGameController inGameController;
    private String siteName;

    @Inject
    public DistrictComponent(){
        this.sitesMap = sitesIconPathsMap;
    }

    @Override
    public void setItem(@NotNull SiteProperties properties) {
        this.siteName = properties.siteName();
        String siteCapacity = properties.siteCapacity();
        this.inGameController = properties.inGameController();

        String imagePath;
        if (Objects.isNull(this.sitesMap.get(this.siteName)))
            imagePath = "de/uniks/stp24/icons/sites/production_site.png";
        else imagePath = this.sitesMap.get(this.siteName);

        this.siteElementButton.setDisable(this.tokenStorage.isSpectator() ||
                !Objects.equals(this.islandAttributeStorage.getIsland().owner(), this.tokenStorage.getEmpireId()));

        this.siteElementButton.setStyle("-fx-background-image: url('/" + imagePath + "'); " +
                "-fx-background-size: 100% 100%;" + "-fx-background-color: transparent;" +
                "-fx-background-repeat: no-repeat;");

        districtCapacity.setText(siteCapacity);
        if (Objects.nonNull(properties.job()))
            this.setJobProgress(properties.job());
    }

    public void showOverview() {
        this.inGameController.showSiteOverview();
        this.inGameController.setSiteType(this.siteName);
    }

    public void setJobProgress(Job job) {
        this.jobProgressBox.setVisible(true);
        this.jobProgress = job.progress();
        this.totalProgress = job.total();
        this.jobTimeText.setText(String.format("%s/%s", this.jobProgress, this.totalProgress));
    }

    public void removeJobProgress() {
        this.jobProgressBox.setVisible(false);
    }

    public void incrementJobProgress() {
        this.jobProgress++;
        this.jobTimeText.setText(String.format("%s/%s", this.jobProgress, this.totalProgress));
    }
}
