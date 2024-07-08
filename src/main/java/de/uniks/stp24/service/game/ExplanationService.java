package de.uniks.stp24.service.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.CustomComponentListCell;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.component.game.VariableExplanationComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Resource;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class ExplanationService {
    @Inject
    public App app;

    private InGameController inGameController;

    @Inject
    public ExplanationService() {

    }

    /*
    Methods below is made for explanation of resources.
     */
    public CustomComponentListCell<Resource, ResourceComponent> addMouseHoverListener(CustomComponentListCell<Resource, ResourceComponent> cell, String component, String listType) {
        VariableExplanationComponent explanationComponent = new VariableExplanationComponent();

        Tooltip tooltip = new Tooltip();
        Tooltip.install(cell, tooltip);
        tooltip.setGraphic(explanationComponent);
        AtomicBoolean entered = new AtomicBoolean(false);

        app.stage().getScene().addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            if (cell.isEmpty() || cell.getItem() == null) {
                tooltip.hide();
                return;
            }

            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();

            Point2D cellStart = cell.localToScreen(0, 0);
            Point2D cellEnd = cell.localToScreen(cell.getWidth() * 2 / 3, cell.getHeight());

            boolean isMouseInsideCell = mouseX < cellEnd.getX() &&
                    mouseX > cellStart.getX() &&
                    mouseY > cellStart.getY() + cell.getHeight() * 2/5 &&
                    mouseY < cellEnd.getY() - cell.getHeight() * 2/5;


            if (isMouseInsideCell && !entered.get()) {
                tooltip.show(app.stage(), mouseX, mouseY);
                entered.set(true);
            } else if (!isMouseInsideCell) {
                tooltip.hide();
                entered.set(false);
            }
        });
        return cell;
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
