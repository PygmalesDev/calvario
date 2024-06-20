package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    public ObservableList<String> items = FXCollections.observableArrayList();

    private final Map<String, String> sites = new HashMap<>();
    private final Map<String, String> buildings = new HashMap<>();
    @Inject
    TokenStorage tokenStorage;

    String structureType;

    @Inject
    public DeleteStructureComponent(){

    }

    @OnInit
    public void init(){
        sites.put("city", "de/uniks/stp24/icons/sites/village_site.png");
        sites.put("energy", "de/uniks/stp24/icons/sites/thaumaturgy_site.png");
        sites.put("mining", "de/uniks/stp24/icons/sites/mining_site.png");
        sites.put("agriculture", "de/uniks/stp24/icons/sites/harvesting_site.png");
        sites.put("industry", "de/uniks/stp24/icons/sites/coalmine_site.png");
        sites.put("research_site", "de/uniks/stp24/icons/sites/epoch_site.png");
        sites.put("ancient_foundry", "de/uniks/stp24/icons/sites/expedition_site.png");
        sites.put("ancient_factory", "de/uniks/stp24/icons/sites/merchant_site.png");
        sites.put("ancient_refinery", "de/uniks/stp24/icons/sites/production_site.png");

        buildings.put("refinery", "de/uniks/stp24/icons/buildings/alloy_smeltery.png");
        buildings.put("factory", "de/uniks/stp24/icons/buildings/theurgy_hall.png");
        buildings.put("foundry", "de/uniks/stp24/icons/buildings/chophouse.png");
        buildings.put("research_lab", "de/uniks/stp24/icons/buildings/resonating_delves.png");
        buildings.put("farm", "de/uniks/stp24/icons/buildings/farmside.png");
        buildings.put("mine", "de/uniks/stp24/icons/buildings/coal_querry.png");
        buildings.put("power_plant", "de/uniks/stp24/icons/buildings/scout_hub.png");
        buildings.put("exchange", "de/uniks/stp24/icons/buildings/seaside_hut.png");
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }


    public void setElements(List<Resource> elements){

    }

    public void setWarningText(){
        warningText.setText("Are you sure you want to delete " + capitalizeFirstLetter(structureType) + "?");
    }

    public void setStructureType(String structureType){
        this.structureType = structureType;
        setWarningText();
        if (sites.containsKey(structureType)) {
            // Set the image for sites
            deleteStructureImageView.setImage(new Image(sites.get(structureType)));
        } else if (buildings.containsKey(structureType)) {
            // Set the image for buildings
            deleteStructureImageView.setImage(new Image(buildings.get(structureType)));
        }
    }

    public void onCancel(){
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
                        inGameController.updateAmountSitesGrid();
                        onCancel();
                    });
                }
            } else if (buildings.containsKey(structureType)) {
                // Handle deletion for buildings
                subscriber.subscribe(resourcesService.destroyBuilding(tokenStorage.getGameId(), island, structureType), result -> {
                    tokenStorage.setIsland(islandsService.updateIsland(result));
                    onCancel();
                });
            } else {
                throw new IllegalArgumentException("Unknown structure type: " + structureType);
            }
        }

    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}


