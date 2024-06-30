package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "TechnologyCategoryDescription.fxml")
public class TechnologyCategoryDescriptionSubComponent extends VBox {

    @FXML
    public ImageView resourceImage;
    @FXML
    public Label descriptionLabel;

    @Inject
    TechnologyService technologyService;

    @Inject
    public TechnologyCategoryDescriptionSubComponent() {

    }
}
