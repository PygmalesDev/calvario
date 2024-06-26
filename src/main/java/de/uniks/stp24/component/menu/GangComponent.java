package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.Gang;
import de.uniks.stp24.model.GangElement;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;

@Component(view = "Gang.fxml")
public class GangComponent extends Pane implements ReusableItemComponent<GangElement> {

    @FXML
    Pane pane;
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
        gangNameTextComponent.setStyle("-fx-text-fill: black;");
        gangNameTextComponent.setText(gang.name());
        pane.setStyle("-fx-background-color: " + gang.color());
        portraitImage.setImage(gangElement.portrait());
    }
}