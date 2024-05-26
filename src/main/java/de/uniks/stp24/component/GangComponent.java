package de.uniks.stp24.component;

import de.uniks.stp24.controllers.GangCreationController;
import de.uniks.stp24.model.Gang;
import de.uniks.stp24.model.GangElement;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.ArrayList;

@Component(view = "Gang.fxml")
public class GangComponent extends Pane implements ReusableItemComponent<GangElement> {

    @FXML
    Pane gangColor;
    @FXML
    Pane pane;
    @FXML
    ImageView flagImage;
    @FXML
    ImageView portraitImage;
    @FXML
    Label gangNameTextComponent;

    @Inject
    public GangComponent() {

    }

    @Override
    public void setItem(@NotNull GangElement gangElement) {
        Gang gang = gangElement.gang();

        java.awt.Color color = java.awt.Color.decode(gang.color());
        //System.out.println(color.getRed() + color.decode("#FFCCEE").getGreen() + color.getBlue());
        if (color.getRed() + color.decode("#FFCCEE").getGreen() + color.getBlue() <= 500)
            gangNameTextComponent.setStyle("-fx-text-fill: white;");

        gangNameTextComponent.setText(gang.name());
        pane.setStyle("-fx-background-color: " + gang.color());
        gangColor.setStyle("-fx-background-color: " + gang.color());
        flagImage.setImage(gangElement.flag());
        portraitImage.setImage(gangElement.portrait());
    }
}