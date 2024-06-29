package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.CustomComponentListCell;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Resource;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class ExplanationService {
    double x;
    double y;
    boolean entered = false;

    private InGameController inGameController;
    /*

    Erstelle eine Methode mit einem Eventlistener oder Interupt, der alle paar
    Sekunden neue Variablen information in Variable Storage speichert

    oder

    rufe die Methode jedesmal auf, wenn die Maus über ein building district oder upgrade drüber hovert.

     */

    @Inject
    public ExplanationService() {

    }

    public CustomComponentListCell<Resource, ResourceComponent> addMouseHoverListener(CustomComponentListCell<Resource, ResourceComponent> cell, String component) {

        cell.setOnMouseMoved(event -> {
            if (!cell.isEmpty() && cell.getItem() != null) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                this.x = cell.localToScene(0, 0).getX();
                this.y = cell.localToScene(0, 0).getY();
                setRootCoordinates(x, y, cell.getWidth(), cell.getHeight(), component);
                entered = mouseX < cell.getWidth() * 2/3 && mouseX >= 4 && mouseY > 4 && mouseY <= cell.getHeight() - 4;
                if(entered) {
                    inGameController.showExplanation(this.x, this.y);
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
