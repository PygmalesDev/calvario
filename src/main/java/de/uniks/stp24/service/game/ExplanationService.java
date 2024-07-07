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

        app.stage().getScene().addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            if (!cell.isEmpty() && cell.getItem() != null) {
                double mouseX = event.getScreenX();
                double mouseY = event.getScreenY();

                Point2D cellStart = cell.localToScreen(0, 0);
                Point2D cellEnd = cell.localToScreen(cell.getWidth() * 2/3, cell.getHeight());

                entered = mouseX < cellEnd.getX() &&
                        mouseX > cellStart.getX() &&
                        mouseY > cellStart.getY() &&
                        mouseY < cellEnd.getY();

                if (entered) {
                    tooltip.setText("Tooltip text");
                    tooltip.show(cell, mouseX, mouseY);
                } else {
                    tooltip.hide();
                }
            }
        });

        return cell;
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
