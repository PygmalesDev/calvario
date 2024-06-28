package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnInit;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "IslandOverviewSites.fxml")
public class OverviewSitesComponent extends AnchorPane {

    @FXML
    public Button detailsButton;
    @FXML
    public Button sitesButton;
    @FXML
    public StackPane sitesContainer;
    @FXML
    public Button buildingsButton;
    @FXML
    public Text island_name;
    @FXML
    public Text island_inf;
    @FXML
    public Text crewCapacity;
    @FXML
    public Text resCapacity;
    @FXML
    public TextField inputIslandName;
    @FXML
    public Button upgradeButton;
    @FXML
    public Pane islandFlag;

    @SubComponent
    @Inject
    public SitesComponent sitesComponent;
    @SubComponent
    @Inject
    public DetailsComponent detailsComponent;
    @SubComponent
    @Inject
    public BuildingsComponent buildingsComponent;
    @Inject
    public InGameService inGameService;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    private InGameController inGameController;

    @Inject
    public OverviewSitesComponent() {

    }

    @OnInit
    public void init(){
        buildingsComponent.setInGameController(inGameController);
        sitesComponent.setInGameController(inGameController);
    }

    public void showDetails() {
        detailsButton.setDisable(true);
        sitesButton.setDisable(false);
        buildingsButton.setDisable(false);

        detailsComponent.setResLists();
        detailsComponent.setSumProduction(islandAttributes.mergeProduction());

        inGameService.showOnly(sitesContainer, detailsComponent);
    }

    public void showUpgrades() {
        setLevelCheckBox();
        if(islandAttributes.getIsland().upgradeLevel() == 4){
            inGameController.overviewUpgradeComponent.upgrade_box.setVisible(false);
            inGameController.overviewUpgradeComponent.upgrade_box.setMouseTransparent(true);
            inGameController.overviewUpgradeComponent.confirmUpgrade.setDisable(true);
        } else {
            inGameController.overviewUpgradeComponent.upgrade_box.setVisible(true);
            inGameController.overviewUpgradeComponent.upgrade_box.setMouseTransparent(false);
            inGameController.overviewUpgradeComponent.confirmUpgrade.setDisable(false);
        }
        inGameService.showOnly(inGameController.overviewContainer, inGameController.overviewUpgradeComponent);
        inGameController.overviewUpgradeComponent.setUpgradeButton();
        inGameController.overviewUpgradeComponent.setNeededResources();
        inGameController.overviewUpgradeComponent.setUpgradeInf();
    }

    public void setLevelCheckBox(){
        switch (islandAttributes.getIsland().upgradeLevel()) {
            case 1 -> {
                inGameController.overviewUpgradeComponent.checkExplored.setVisible(true);
                inGameController.overviewUpgradeComponent.checkColonized.setVisible(false);
                inGameController.overviewUpgradeComponent.checkUpgraded.setVisible(false);
                inGameController.overviewUpgradeComponent.checkDeveloped.setVisible(false);
            }
            case 2 -> {
                inGameController.overviewUpgradeComponent.checkExplored.setVisible(true);
                inGameController.overviewUpgradeComponent.checkColonized.setVisible(true);
                inGameController.overviewUpgradeComponent.checkUpgraded.setVisible(false);
                inGameController.overviewUpgradeComponent.checkDeveloped.setVisible(false);
            }
            case 3 -> {
                inGameController.overviewUpgradeComponent.checkExplored.setVisible(true);
                inGameController.overviewUpgradeComponent.checkColonized.setVisible(true);
                inGameController.overviewUpgradeComponent.checkUpgraded.setVisible(true);
                inGameController.overviewUpgradeComponent.checkDeveloped.setVisible(false);
            }
            case 4 -> {
                inGameController.overviewUpgradeComponent.checkExplored.setVisible(true);
                inGameController.overviewUpgradeComponent.checkColonized.setVisible(true);
                inGameController.overviewUpgradeComponent.checkUpgraded.setVisible(true);
                inGameController.overviewUpgradeComponent.checkDeveloped.setVisible(true);
            }
        }
    }

    public void showBuildings() {
        buildingsComponent.setInGameController(inGameController);
        buildingsButton.setDisable(true);
        sitesButton.setDisable(false);
        detailsButton.setDisable(false);
        buildingsComponent.setGridPane();
        inGameService.showOnly(sitesContainer, buildingsComponent);
    }

    public void showSites() {
        detailsButton.setDisable(false);
        sitesButton.setDisable(true);
        buildingsButton.setDisable(false);
        sitesComponent.setSitesBox(islandAttributes.getIsland());
        inGameService.showOnly(sitesContainer, sitesComponent);
    }

    public void setContainer() {
        sitesContainer.setVisible(false);
        sitesContainer.getChildren().add(sitesComponent);
        sitesContainer.getChildren().add(detailsComponent);
        sitesContainer.getChildren().add(buildingsComponent);
    }

    public void setIngameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void closeOverview() {
        resetButtons();
        inGameController.buildingsWindowComponent.setVisible(false);
        inGameController.sitePropertiesComponent.setVisible(false);
        inGameController.buildingPropertiesComponent.setVisible(false);
        inGameController.overviewContainer.setVisible(false);
        inGameController.selectedIsland.islandIsSelected = false;

        if(!inGameController.islandsService.keyCodeFlag) {
            inGameController.selectedIsland.flagPane.setVisible(!inGameController.selectedIsland.flagPane.isVisible());
            inGameController.selectedIsland.rudderImage.setVisible(false);
        }

        inGameController.selectedIsland = null;
    }

    public void resetButtons(){
        detailsButton.setDisable(false);
        sitesButton.setDisable(false);
    }

    public void updateResCapacity(){
        int usedSlots = sitesComponent.getTotalSiteSlots(islandAttributes.getIsland()) +
                islandAttributes.getIsland().buildings().size();
        islandAttributes.setUsedSlots(usedSlots);

        resCapacity.setText(usedSlots + "/" + islandAttributes.getIsland().resourceCapacity());
    }

    public void setOverviewSites() {
        islandFlag.setStyle("-fx-background-image: url('" + inGameController.flagsPath.get(islandAttributes.getIsland().flagIndex()) +"');" +
                "-fx-background-size: 100% 100%;" + "-fx-background-repeat: no-repeat;");
        showBuildings();
        upgradeButton.setDisable(!Objects.equals(islandAttributes.getIsland().owner(), inGameController.tokenStorage.getEmpireId()));

        updateResCapacity();

        island_name.setText(islandAttributes.getIslandNameTranslated() + "(" + islandAttributes.getUpgradeTranslation(islandAttributes.getIsland().upgradeLevel()) + ")");
        crewCapacity.setText(String.valueOf(islandAttributes.getIsland().crewCapacity()));


        switch (islandAttributes.getIsland().upgradeLevel()) {
            case 1 -> island_inf.setText(islandAttributes.upgradeEffects.get(1));
            case 2 -> island_inf.setText(islandAttributes.upgradeEffects.get(2));
            case 3 -> island_inf.setText(islandAttributes.upgradeEffects.get(3));
            case 4 -> island_inf.setText(islandAttributes.upgradeEffects.get(4));
        }
    }
}