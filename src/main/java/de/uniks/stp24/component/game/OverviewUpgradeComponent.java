package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

@Component(view = "IslandOverviewUpgrade.fxml")
public class OverviewUpgradeComponent extends AnchorPane {
    @FXML
    public Text report;
    @FXML
    public Text res_4;
    @FXML
    public Text res_3;
    @FXML
    public Text res_2;
    @FXML
    public Text res_1;
    @FXML
    public Pane confirmUpgrade;
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
    public Pane closeButton;
    @FXML
    public Pane backButton;
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

    public GameSystemsApiService gameSystemsService;

    private InGameController inGameController;

    @Inject
    public OverviewUpgradeComponent() {

    }

    public void setUpgradeButton() {
        if (islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel()) != null) {
            if (resourcesService.hasEnoughResources(islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel()))) {
                confirmUpgrade.setStyle("-fx-background-color: green;");
            } else {
                confirmUpgrade.setStyle("-fx-background-color: black;");
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
        if (islandAttributes.getIsland().flagIndex() >= 0) {
            inGameController.selectedIsland.flagPane.setVisible(!inGameController.selectedIsland.flagPane.isVisible());
        }
        inGameController.selectedIsland = null;
    }

    public void setIngameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void setNeededResources() {
        if (inGameController != null) {
            LinkedList<Text> resTextList = new LinkedList<>(Arrays.asList(res_1, res_2, res_3, res_4, report));
            int i = 0;
            for (Map.Entry<String, Integer> entry : islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel()).entrySet()) {
                resTextList.get(i).setText(entry.getKey() + " " + entry.getValue());
                i += 1;
            }
        }
    }

    public void upgradeIsland() {
        if (resourcesService.hasEnoughResources(islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel()))) {
            resourcesService.upgradeIsland();
            setNeededResources();
            String upgradeStatus = switch (islandAttributes.getIsland().upgradeLevel()) {
                case 0 -> islandAttributes.systemPresets.explored().id();
                case 1 -> islandAttributes.systemPresets.colonized().id();
                case 2 -> islandAttributes.systemPresets.upgraded().id();
                case 3 -> islandAttributes.systemPresets.developed().id();
                default -> null;
            };
            islandsService.upgradeSystem(islandAttributes, upgradeStatus, inGameController);
        }
    }

    public void setUpgradeInf() {
        levelOne.setText(islandAttributes.getUpgradeTranslation(1));
        levelTwo.setText(islandAttributes.getUpgradeTranslation(2));
        levelThree.setText(islandAttributes.getUpgradeTranslation(3));
        levelFour.setText(islandAttributes.getUpgradeTranslation(4));
        levelTwoText.setText(islandAttributes.upgradeEffects.get(2));
        levelThreeText.setText(islandAttributes.upgradeEffects.get(3));
        levelFourText.setText(islandAttributes.upgradeEffects.get(3));
    }
}
