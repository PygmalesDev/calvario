package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.CreateSystemsDto;
import de.uniks.stp24.dto.UpdateSystemDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String updatedAt = LocalDateTime.now().format(formatter);
            Island tmp1 = islandAttributes.getIsland();
            String upgradeStatus = switch (tmp1.upgradeLevel()) {
                case 0 -> islandAttributes.systemPresets.explored().id();
                case 1 -> islandAttributes.systemPresets.colonized().id();
                case 2 -> islandAttributes.systemPresets.upgraded().id();
                case 3 -> islandAttributes.systemPresets.developed().id();
                default -> null;
            };


            this.subscriber.subscribe(gameSystemsService.updateIsland(tokenStorage.getGameId(), tokenStorage.getEmpireId(),
                    new UpdateSystemDto(
                            null,
                            tmp1.sites(),
                            tmp1.buildings(),
                            upgradeStatus,
                            tokenStorage.getEmpireId())), result -> {

                Island tmp2 = new Island(
                        result._id(),
                        result.owner(),
                        Objects.isNull(result.owner()) ? -1 : tokenStorage.getFlagIndex(result.owner()),
                        result.x(),
                        result.y(),
                        IslandType.valueOf(String.valueOf(result.type())),
                        result.population(),
                        result.capacity(),
                        Upgrade.valueOf(result.upgrade()).ordinal(),
                        result.districtSlots(),
                        result.districts(),
                        result.buildings()
                );
                inGameController.selectedIsland.island = tmp2;
                islandAttributes.setIsland(tmp2);
            });
            inGameController.showOverview(islandAttributes.getIsland());
        }
    }
}
