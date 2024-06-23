package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.BuildingDto;
import de.uniks.stp24.dto.SiteDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.*;

@Component(view = "DeleteStructureWarning.fxml")
public class DeleteStructureComponent extends VBox{
    @FXML
    ListView<Resource> deleteStructureListView;
    @FXML
    ImageView deleteStructureImageView;
    @FXML
    Button confirmButton;
    @FXML
    Button cancelButton;
    @FXML
    Text warningText;
    @FXML
    VBox warningContainer;
    @Inject
    Subscriber subscriber;

    @Inject
    IslandsService islandsService;
    @Inject
    ResourcesService resourcesService;

    InGameController inGameController;

    @Inject
    App app;


    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    public ObservableList<String> items = FXCollections.observableArrayList();

    public Map<String, String> sites = sitesIconPathsMap;
    public final Map<String, String> buildings = buildingsIconPathsMap;
    @Inject
    TokenStorage tokenStorage;

    @Inject
    IslandAttributeStorage islandAttributeStorage;

    Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, false, true, false, gameResourceBundle);


    String structureType;

    @Inject
    public DeleteStructureComponent(){
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }


    public void setWarningText(){
        if(buildings.containsKey(structureType)){
            warningText.setText(gameResourceBundle.getString("sure.you.want.delete") + " " + gameResourceBundle.getString( buildingTranslation.get(structureType)) + "?");
        }else{
            warningText.setText(gameResourceBundle.getString("sure.you.want.delete") + " " + gameResourceBundle.getString( siteTranslation.get(structureType)) + "?");
        }
    }

    public void handleDeleteStructure(String structureType){
        this.structureType = structureType;
        setWarningText();
        displayStructureInfo();
        if (sites.containsKey(structureType)) {
            // Set the image for sites
            deleteStructureImageView.setImage(new Image(sites.get(structureType)));
        } else if (buildings.containsKey(structureType)) {
            // Set the image for buildings
            deleteStructureImageView.setImage(new Image(buildings.get(structureType)));
        }
    }

    private void displayStructureInfo() {
        if (buildings.containsKey(structureType)){
            subscriber.subscribe(resourcesService.getResourcesBuilding(structureType), this::resourceListGenerationBuilding);
        }
        if (sites.containsKey(structureType)){
            subscriber.subscribe(resourcesService.getResourcesSite(structureType), this::resourceListGenerationSite);
        }

        deleteStructureListView.setCellFactory(list -> new ComponentListCell<>(app, resourceComponentProvider));
    }

    private void resourceListGenerationBuilding(BuildingDto structureDto) {
        Map<String, Integer> resourceMapCost = structureDto.cost();
        Map<String, Integer> halvedResourceMapCost = new HashMap<>();
        for (Map.Entry<String, Integer> entry : resourceMapCost.entrySet()) {
            halvedResourceMapCost.put(entry.getKey(), entry.getValue() / 2);
        }
        ObservableList<Resource> resourceListCost = resourcesService.generateResourceList(halvedResourceMapCost, deleteStructureListView.getItems(), null);
        deleteStructureListView.setItems(resourceListCost);
    }
    private void resourceListGenerationSite(SiteDto structureDto) {
        Map<String, Integer> resourceMapCost = structureDto.cost();
        Map<String, Integer> halvedResourceMapCost = new HashMap<>();
        for (Map.Entry<String, Integer> entry : resourceMapCost.entrySet()) {
            halvedResourceMapCost.put(entry.getKey(), entry.getValue() / 2);
        }
        ObservableList<Resource> resourceListCost = resourcesService.generateResourceList(halvedResourceMapCost, deleteStructureListView.getItems(), null);
        deleteStructureListView.setItems(resourceListCost);
    }

    public void onCancel(){
        inGameController.handleAfterStructureDelete();
        setVisible(false);
    }

    public void delete(){
        Island island = tokenStorage.getIsland();
        if (tokenStorage.getIsland() != null){
            if (sites.containsKey(structureType)) {
                // Handle deletion for sites
                if (tokenStorage.getIsland().sites().get(structureType) != 0) {
                    subscriber.subscribe(resourcesService.destroySite(tokenStorage.getGameId(), island, structureType), result -> {
                        tokenStorage.setIsland(islandsService.updateIsland(result));
                        islandAttributeStorage.setIsland(islandsService.updateIsland(result));
                        inGameController.updateAmountSitesGrid();
                        inGameController.updateSiteCapacities();
                        onCancel();
                    });
                }
            } else if (buildings.containsKey(structureType)) {
                // Handle deletion for buildings
                subscriber.subscribe(resourcesService.destroyBuilding(tokenStorage.getGameId(), island, structureType), result -> {
                    tokenStorage.setIsland(islandsService.updateIsland(result));
                    islandAttributeStorage.setIsland(islandsService.updateIsland(result));
                    inGameController.islandsService.updateIslandBuildings(islandAttributeStorage, inGameController, islandAttributeStorage.getIsland().buildings());
                    inGameController.setSitePropertiesInvisible();
                    onCancel();
                });
            } else {
                throw new IllegalArgumentException("Unknown structure type: " + structureType);
            }
        }

    }
}