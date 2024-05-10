package de.uniks.stp24.component;

import de.uniks.stp24.model.Gang;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "Gang.fxml")
public class GangComponent extends Pane implements ReusableItemComponent<Gang> {

    @FXML
    Pane pane;
    @FXML
    ImageView flagImage;
    @FXML
    ImageView portraitImage;
    @FXML
    Text gangNameText;

    @Inject
    public GangComponent() {

    }

    @Override
    public void setItem(@NotNull Gang gang) {
        gangNameText.setText(gang.name());
        if (gang.color().getRed() + gang.color().getGreen() + gang.color().getBlue() <= 1)
            gangNameText.setFill(Color.WHITE);
        pane.setStyle("-fx-background-color: #" + gang.color().toString().substring(2));
        flagImage.setImage(new Image(gang.flag()));
        portraitImage.setImage(new Image(gang.portrait()));

    }
}