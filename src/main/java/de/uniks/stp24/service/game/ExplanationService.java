package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.CustomComponentListCell;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Resource;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ExplanationService {

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
        cell.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
            if (!cell.isEmpty() && cell.getItem() != null) {
                inGameController.app.stage().getScene().setOnMouseMoved(movingEvent -> {
                    double x = setRootCoordinateX(movingEvent.getSceneX(), component);
                    double y = setRootCoordinateY(movingEvent.getY(), component);
                });
            }
        });

        cell.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
            if (!cell.isEmpty() && cell.getItem() != null) {

            }
        });
        return cell;
    }

    public double setRootCoordinateX(double x, String component){
        return 0;
    }

    public double setRootCoordinateY(double y, String component){
        return 0;
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
