package de.uniks.stp24.service.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.CustomComponentListCell;
import de.uniks.stp24.component.game.ExplanationComponent;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.component.game.VariableExplanationComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.ExplainedVariableDTO;
import de.uniks.stp24.model.Effect;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.model.Sources;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class ExplanationService {
    @Inject
    public App app;
    @Inject
    public VariableService variableService;

    private InGameController inGameController;

    @Inject
    public ExplanationService() {

    }

    /*
    Methods below is made for explanation of resources.
     */
    public CustomComponentListCell<Resource, ResourceComponent> addMouseHoverListener(CustomComponentListCell<Resource, ResourceComponent> cell, String listTyp, String indicator, String resourceCategory) {
        VariableExplanationComponent explanationComponent = new VariableExplanationComponent(app);

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

            boolean isMouseInsideCell = false;
            if (cellEnd != null) {
                isMouseInsideCell = mouseX < cellEnd.getX() && mouseX > cellStart.getX() && mouseY > cellStart.getY() + cell.getHeight() * 2 / 5 && mouseY < cellEnd.getY() - cell.getHeight() * 2 / 5;
            }


            if (isMouseInsideCell && !entered.get()) {
                initializeResExplanation(listTyp, indicator, resourceCategory, cell.getItem().resourceID(), explanationComponent);
                tooltip.show(app.stage(), mouseX, mouseY);
                entered.set(true);
            } else if (!isMouseInsideCell) {
                tooltip.hide();
                entered.set(false);
            }
        });
        return cell;
    }

    private void initializeResExplanation(String listType, String indicator, String ResCategory, String id, VariableExplanationComponent variableExplanationComponent) {
        String variable = listType + "." + indicator + "." + ResCategory + "." + id;
        ExplainedVariableDTO explanation = variableService.data.get(variable);
        variableExplanationComponent.setValues("Base: " + explanation.initial(), "Total: " + explanation.finalValue());

        List<ExplanationComponent> explanationComponentList = new ArrayList<>();
        if(!variableService.getActiveEffects().get(variable).isEmpty()){
            for(String effect: variableService.getActiveEffects().get(variable)){

            }
        }

        //TODO: Frage die size der effekte ab und iteriere über die Größe um die effect types anzuzeigen.
        variableExplanationComponent.fillListWithEffects(explanationComponentList);

        ExplainedVariableDTO explanations = variableService.data.get(variable);
        Map<String, Double> effects = new HashMap<>();

        for (Sources source : explanations.sources()) {
            for (Effect effect : source.effects()) {
                effects.put(effect.variable(), effect.multiplier());
            }
        }
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
