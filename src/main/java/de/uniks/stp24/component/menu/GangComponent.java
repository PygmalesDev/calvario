package de.uniks.stp24.component.menu;

import de.uniks.stp24.controllers.GangCreationController;
import de.uniks.stp24.model.Gang;
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
    Label gangNameTextComponent;

    ArrayList<Image> flagsList = new ArrayList<>();
    ArrayList<Image> portraitsList = new ArrayList<>();
    String resourcesPaths = "/de/uniks/stp24/assets/";
    String flagsFolderPath = "flags/flag_";
    String portraitsFolderPath = "portraits/captain_";
    int imagesCount = 16;

    @Inject
    public GangComponent() {

    }

    private void initImages() {
        for (int i = 0; i <= imagesCount; i++) {
            InputStream flagStream = GangCreationController.class.getResourceAsStream(resourcesPaths + flagsFolderPath + i + ".png");
            InputStream portraitStream = GangCreationController.class.getResourceAsStream(resourcesPaths + portraitsFolderPath + i + ".png");
            GangCreationController.addImagesToList(flagStream, flagsList);
            GangCreationController.addImagesToList(portraitStream, portraitsList);
        }
    }

    @Override
    public void setItem(@NotNull Gang gang) {
        if (flagsList.size() == 0 || portraitsList.size() == 0)
            initImages();

        java.awt.Color color = java.awt.Color.decode(gang.color());
        //System.out.println(color.getRed() + color.decode("#FFCCEE").getGreen() + color.getBlue());
        if (color.getRed() + color.decode("#FFCCEE").getGreen() + color.getBlue() <= 500)
            gangNameTextComponent.setStyle("-fx-text-fill: white;");

        gangNameTextComponent.setText(gang.name());
        pane.setStyle("-fx-background-color: " + gang.color());
        gangColor.setStyle("-fx-background-color: " + gang.color());
        flagImage.setImage(flagsList.get(gang.flagIndex()));
        portraitImage.setImage(portraitsList.get(gang.portraitIndex()));
    }
}