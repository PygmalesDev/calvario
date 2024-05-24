package de.uniks.stp24.component;

import de.uniks.stp24.model.Gang;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "Gang.fxml")
public class GangComponent extends Pane implements ReusableItemComponent<Gang> {

    @FXML
    Pane gangColor;
    @FXML
    Pane pane;
    @FXML
    ImageView flagImage;
    @FXML
    ImageView portraitImage;
    @FXML
    Label gangNameText;

    @Inject
    public GangComponent() {

    }

    @Override
    public void setItem(@NotNull Gang gang) {
        java.awt.Color color = java.awt.Color.decode(gang.color());
        //System.out.println(color.getRed() + color.decode("#FFCCEE").getGreen() + color.getBlue());
        if (color.getRed() + color.decode("#FFCCEE").getGreen() + color.getBlue() <= 500)
            gangNameText.setStyle("-fx-text-fill: white;");

        gangNameText.setText(gang.name());
        pane.setStyle("-fx-background-color: " + gang.color());
        gangColor.setStyle("-fx-background-color: " + gang.color());
        flagImage.setImage(new Image(gang.flag()));
        portraitImage.setImage(new Image(gang.portrait()));
    }
}