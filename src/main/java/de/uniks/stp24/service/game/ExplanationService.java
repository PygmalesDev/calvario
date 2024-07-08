package de.uniks.stp24.service.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.CustomComponentListCell;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Resource;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.ArrayList;

@Singleton
public class ExplanationService {
    @Inject
    public App app;
    double x;
    double y;
    boolean entered = false;

    private InGameController inGameController;

    @Inject
    public ExplanationService() {

    }

    /*
    Methods below is made for explanation of resources.
     */
    public CustomComponentListCell<Resource, ResourceComponent> addMouseHoverListener(CustomComponentListCell<Resource, ResourceComponent> cell, String component, String listType) {
        Tooltip tooltip = new Tooltip();
        Tooltip.install(cell, tooltip);
        tooltip.setGraphic(inGameController.variableExplanationComponent);

        cell.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if (cell.isEmpty() || cell.getItem() == null) {
                tooltip.hide();
                return;
            }

            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();

            tooltip.show(app.stage(), mouseX, mouseY);
        });

        cell.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            tooltip.hide();
        });

        return cell;
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
