package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ExplanationService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import de.uniks.stp24.model.Resource;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.*;

@Component(view = "IslandOverviewUpgrade.fxml")
public class OverviewUpgradeComponent extends AnchorPane {
    @FXML
    public Button confirmUpgrade;
    @FXML
    public HBox upgrade_box;
    @FXML
    public Pane checkExplored;
    @FXML
    public Pane checkColonized;
    @FXML
    public Pane checkUpgraded;
    @FXML
    public Pane checkDeveloped;
    @FXML
    public Text levelOne;
    @FXML
    public Text levelTwo;
    @FXML
    public Label levelTwoText;
    @FXML
    public Text levelThree;
    @FXML
    public Label levelThreeText;
    @FXML
    public Text levelFour;
    @FXML
    public Label levelFourText;
    @FXML
    public ListView upgradeUpkeepList;
    @FXML
    public ListView upgradeCostList;

    @Inject
    InGameService inGameService;
    @Inject
    ResourcesService resourcesService;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @Inject
    IslandsService islandsService;
    @Inject
    public ExplanationService explanationService;
    @Inject
    App app;
    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;
    @Inject
    JobsService jobsService;

    public GameSystemsApiService gameSystemsService;

    private InGameController inGameController;

    Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, false, true, false, gameResourceBundle);

    @Inject
    public OverviewUpgradeComponent() {

    }

    public void setUpgradeButton() {
        if (Objects.nonNull(this.islandAttributes.getIsland())) {
            if (islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel()) != null) {
                if (resourcesService.hasEnoughResources(islandAttributes
                        .getNeededResources(islandAttributes.getIsland().upgradeLevel()))) {
                    confirmUpgrade.setStyle("-fx-background-image: url('/de/uniks/stp24/assets/buttons/upgrade_button_on.png'); " +
                            "-fx-background-size: cover;" + "-fx-background-color: transparent");
                } else {
                    confirmUpgrade.setStyle("-fx-background-image: url('/de/uniks/stp24/assets/buttons/upgrade_button_off.png'); " +
                            "-fx-background-size: cover;" + "-fx-background-color: transparent");
                }
            }
        }
    }

    public void goBack() {
        inGameService.showOnly(inGameController.overviewContainer, inGameController.overviewSitesComponent);
    }

    public void closeOverview() {
        inGameController.overviewContainer.setVisible(false);
        inGameController.selectedIsland.rudderImage.setVisible(false);
        inGameController.selectedIsland.islandIsSelected = false;
        if(!islandsService.keyCodeFlag) {
            inGameController.selectedIsland.flagPane.setVisible(!inGameController.selectedIsland.flagPane.isVisible());
        }
        inGameController.selectedIsland = null;
    }

    public void setIngameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void setListViews() {
        setCosts();
        setConsumes();
    }

    public void setCosts(){
        upgradeCostList.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, resourceComponentProvider), "systems", islandAttributes.getIsland().upgrade(), "cost"));
        Map<String, Integer> resourceMapCost = islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel());
        ObservableList<Resource> resourceListCost = resourcesService.generateResourceList(resourceMapCost, upgradeCostList.getItems(), null);
        upgradeCostList.setItems(resourceListCost);
    }

    public void setConsumes(){
        upgradeUpkeepList.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, resourceComponentProvider), "systems", islandAttributes.getIsland().upgrade(), "upkeep"));
        Map<String, Integer> resourceMapUpkeep = islandAttributes.getUpkeep(islandAttributes.getIsland().upgradeLevel());
        ObservableList<Resource> resourceListUpkeep = resourcesService.generateResourceList(resourceMapUpkeep, upgradeUpkeepList.getItems(), null);
        upgradeUpkeepList.setItems(resourceListUpkeep);
    }

    @OnInit
    public void setIslandUpgradeFinishers() {
        this.jobsService.onJobsLoadingFinished("upgrade", job ->
                this.jobsService.onJobCompletion(job._id(), () -> this.islandsService.updateIsland(job.system())));
    }

    public void setUpgradeInf() {
        levelOne.setText(islandAttributes.getUpgradeTranslation(1));
        levelTwo.setText(islandAttributes.getUpgradeTranslation(2));
        levelThree.setText(islandAttributes.getUpgradeTranslation(3));
        levelFour.setText(islandAttributes.getUpgradeTranslation(4));
        levelTwoText.setText(islandAttributes.upgradeEffects.get(2));
        levelThreeText.setText(islandAttributes.upgradeEffects.get(3));
        levelFourText.setText(islandAttributes.upgradeEffects.get(4));
    }
}
