package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "TechnologyCategoryDescription.fxml")
public class TechnologyCategoryDescriptionSubComponent {

    @FXML
    public ImageView resourceImage;
    @FXML
    public Label descriptionLabel;

    @Inject
    public TechnologyCategoryDescriptionSubComponent() {

    }
}
