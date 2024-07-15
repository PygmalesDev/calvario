package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import javax.inject.Provider;

import java.io.IOException;
import java.util.List;

@Component(view = "VariableExplanationOverview.fxml")
public class VariableExplanationComponent extends VBox {

    @FXML
    ListView effectList;
    @FXML
    Text baseValue;
    @FXML
    Text totalValue;
    @FXML
    Text title;

    App app;

    ObservableList<String> explanations = FXCollections.observableArrayList();

    public VariableExplanationComponent(App app) {
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
        effectList.setItems(explanations);
    }

    public void fillListWithEffects(List<String> newEffects) {
        explanations.setAll(newEffects);
    }

    public void setValues(String baseValue, String totalValue, String res) {
        this.title.setText(res);
        this.baseValue.setText(baseValue);
        this.totalValue.setText(totalValue);
    }
}
