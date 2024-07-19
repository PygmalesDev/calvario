package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.*;

@Component(view = "IslandOverviewUpgrade.fxml")
public class OverviewUpgradeComponent extends AnchorPane {
    @FXML
    public Text res_2;
    @FXML
    public Text res_1;
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
    public Pane res1;
    @FXML
    public Pane res2;
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
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    public GameSystemsApiService gameSystemsService;

    private InGameController inGameController;

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

    public void setNeededResources() {
        if (inGameController != null) {
            LinkedList<Text> resTextList = new LinkedList<>(Arrays.asList(res_1, res_2));
            ArrayList<Pane> resPic = new ArrayList<>(Arrays.asList(res1, res2));
            int i = 0;
            for (Map.Entry<String, Integer> entry : islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel()).entrySet()) {
                resTextList.get(i).setText(String.valueOf(entry.getValue()));
                String sourceImage = switch (entry.getKey()) {
                    case "minerals" -> "-fx-background-image: url('/de/uniks/stp24/icons/resources/minerals.png'); ";
                    case "energy" -> "-fx-background-image: url('/de/uniks/stp24/icons/resources/energy.png'); ";
                    case "alloys" -> "-fx-background-image: url('/de/uniks/stp24/icons/resources/alloys.png'); ";
                    case "fuel" -> "-fx-background-image: url('/de/uniks/stp24/icons/resources/fuel.png'); ";
                    default -> "";
                };
                resPic.get(i).setStyle(sourceImage +
                        "-fx-background-size: cover;");
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
        levelFourText.setText(islandAttributes.upgradeEffects.get(4));
    }
}
