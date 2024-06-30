package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;

@Component(view = "TechnologyCategorySubComponent.fxml")
public class TechnologyCategorySubComponent extends VBox implements ReusableItemComponent<TechnologyExtended> {
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

    TechnologyExtended technology;

    @Inject
    TechnologyService technologyService;

    @Inject
    @SubComponent
    TechnologyCategoryDescriptionSubComponent descriptionComponent;

    Provider<TechnologyCategoryDescriptionSubComponent> descriptionComponentProvider = TechnologyCategoryDescriptionSubComponent::new;

    @Inject
    public TechnologyCategorySubComponent() {
    }

    @Override
    public void setItem(@NotNull TechnologyExtended technologyExtended) {
        this.technology = technologyExtended;

    }
}
