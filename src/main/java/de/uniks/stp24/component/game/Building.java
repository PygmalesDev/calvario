package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import javafx.scene.image.ImageView;
import java.io.IOException;

@Component(view = "buildingElement.fxml")
public class Building extends VBox {
    @FXML
    private ImageView building;

    private String buildingName;
    private BuildingsComponent buildingsComponent;

    @Inject
    public Building(BuildingsComponent buildingsComponent, String buildingName){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("buildingElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.buildingName = buildingName;
        this.buildingsComponent = buildingsComponent;

        building.setOnMouseClicked(event -> {
            System.out.println("Building " + buildingName + " was clicked!");
        });
    }

}
