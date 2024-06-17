package de.uniks.stp24.component.game;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Map;

@Component(view = "details.fxml")
public class DetailsComponent extends VBox {
    @FXML
    public ListView sumProduction;
    @FXML
    public ListView sumConsumption;
    @Inject
    InGameService inGameService;
    @Inject
    ResourcesService resourcesService;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;

    @Inject
    public DetailsComponent(){

    }

    public void setSumProduction(Map<String, Integer> totalProduction){
        ObservableList<String> items = sumProduction.getItems();

        // Lösche alle vorhandenen Einträge in der ListView
        items.clear();

        for (Map.Entry<String, Integer> entry : totalProduction.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            String item = key + ": " + value;

            items.add(item);
        }
    }

    public void setSumConsumption(Map<String, Integer> totalConsumption){
        ObservableList<String> items = sumConsumption.getItems();

        // Lösche alle vorhandenen Einträge in der ListView
        items.clear();

        for (Map.Entry<String, Integer> entry : totalConsumption.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            String item = key + ": " + value;

            items.add(item);
        }
    }
}
