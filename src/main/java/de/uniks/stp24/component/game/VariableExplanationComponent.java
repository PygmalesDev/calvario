package de.uniks.stp24.component.game;
import de.uniks.stp24.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import javax.inject.Provider;

import java.io.IOException;
import java.util.List;

@Component(view = "VariableExplanationOverview.fxml")
public class VariableExplanationComponent extends VBox {

    @FXML
    ListView effectList;

    App app;

    Provider<ExplanationComponent> ExplanationComponentProvider = ExplanationComponent::new;
    ObservableList<ExplanationComponent> explanations = FXCollections.observableArrayList();

    public VariableExplanationComponent(App app){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VariableExplanationOverview.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.app = app;
        initialize();
    }

    private void initialize() {
        effectList.setCellFactory(list -> new CustomComponentListCell<>(app, ExplanationComponentProvider));
        effectList.setItems(explanations);
    }

    public void fillListWithEffects(List<ExplanationComponent> newEffects){
        explanations.setAll(newEffects);
    }

    private boolean noEffects(){
        return false;
    }
}
