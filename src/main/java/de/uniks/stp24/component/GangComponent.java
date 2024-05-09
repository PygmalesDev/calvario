package de.uniks.stp24.component;

import dagger.Subcomponent;
import de.uniks.stp24.model.Gang;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Subcomponent(view = "gangComponent.fxml")
public class GangComponent extends Pane implements ReusableItemComponent<Gang> {

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
    }
}