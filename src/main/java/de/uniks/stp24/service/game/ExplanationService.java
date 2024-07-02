package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.CustomComponentListCell;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Resource;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.ArrayList;

@Singleton
public class ExplanationService {
    double x;
    double y;
    boolean entered = false;

    private InGameController inGameController;
    public ArrayList<String> allVariables = new ArrayList<>();

    @Inject
    public ExplanationService() {

    }

    /*
    Methods below is made for explanation of resources.
     */
    public CustomComponentListCell<Resource, ResourceComponent> addMouseHoverListener(CustomComponentListCell<Resource, ResourceComponent> cell, String component, String listType) {
        cell.setOnMouseMoved(event -> {
            if (!cell.isEmpty() && cell.getItem() != null) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                this.x = cell.localToScene(0, 0).getX();
                this.y = cell.localToScene(0, 0).getY();
                setRootCoordinates(x, y, cell.getWidth(), cell.getHeight(), component);
                entered = mouseX < cell.getWidth() * 2/3 && mouseX >= 4 && mouseY > 4 && mouseY <= cell.getHeight() - 4;
                if(entered) {
                    String variable = "";
                    switch(listType){
                        case "building.costs":
                            variable = "buildings." + inGameController.selectedBuilding + ".cost." + cell.getItem().resourceID();
                            break;
                        case "building.production":
                            variable = "buildings." + inGameController.selectedBuilding + ".production." + cell.getItem().resourceID();
                            break;
                        case "building.upkeep":
                            variable = "buildings." + inGameController.selectedBuilding + ".upkeep." + cell.getItem().resourceID();
                            break;
                        case "site.costs":
                            variable = "districts." + inGameController.selectedSites + ".cost." + cell.getItem().resourceID();
                            break;
                        case "site.consumption":
                            variable = "districts." + inGameController.selectedSites + ".upkeep." + cell.getItem().resourceID();
                            break;
                        case "site.production":
                            variable = "districts." + inGameController.selectedSites + ".production." + cell.getItem().resourceID();
                            break;
                        case "upgrade.costs":
                            variable = "systems." + inGameController.islandAttributes.getIsland().upgrade() + ".cost." + cell.getItem().resourceID();
                            break;
                        case "upgrade.upkeep":
                            variable = "systems." + inGameController.islandAttributes.getIsland().upgrade() + ".upkeep." + cell.getItem().resourceID();
                            break;
                    }
                    inGameController.showExplanation(this.x, this.y, variable);
                } else {
                    inGameController.unShowExplanation();
                }
            }
        });

        return cell;
    }

    public void setRootCoordinates(double x, double y, double cellWidth, double cellHeight, String component){
        switch (component){
            case "islandOverview":
                this.x = x - inGameController.explanationContainer.getWidth();
                this.y = y - inGameController.explanationContainer.getHeight() + cellHeight / 2;
                break;
            case "storageOverview":
                this.x = x + cellWidth * 2/3;
                this.y = y + cellHeight / 2;
                break;
        }

    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
