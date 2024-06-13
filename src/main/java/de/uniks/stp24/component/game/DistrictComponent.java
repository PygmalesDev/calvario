package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.io.IOException;

@Component(view = "DistrictComponent.fxml")
public class DistrictComponent extends AnchorPane {

    @FXML
    public Text districtName;
    @FXML
    public Text districtCapacity;

    @Inject
    public DistrictComponent(String name, String capacity){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DistrictComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        districtName.setText(name);
        districtCapacity.setText(capacity);
    }



}
