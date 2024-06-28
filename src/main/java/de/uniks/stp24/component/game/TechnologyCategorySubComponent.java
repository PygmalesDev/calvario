package de.uniks.stp24.component.game;

import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import javax.inject.Provider;

@Component(view = "TechnologyCategorySubComponent.fxml")
public class TechnologyCategorySubComponent {
    @FXML
    public ImageView tagImage3;
    @FXML
    public ImageView tagImage2;
    @FXML
    public ImageView tagImage1;
    @FXML
    public ListView<TechnologyExtended> descriptionListView;
    @FXML
    public ImageView timeImage;
    @FXML
    public Label timeLabel;
    @FXML
    public ImageView researchImage;
    @FXML
    public Label researchLabel;
    @FXML
    public Button researchButton;

    @Inject
    TechnologyService technologyService;

    Provider<TechnologyCategoryDescriptionSubComponent> descriptionComponentProvider = TechnologyCategoryDescriptionSubComponent::new;

    @Inject
    public TechnologyCategorySubComponent() {

    }
}
