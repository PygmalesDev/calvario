package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.App;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
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

//    @FXML
//    public ListView<TechnologyExtended> descriptionListView;
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
    @FXML
    public HBox researchHBox;
    @FXML
    public Label technologyLabel;

    @Inject
    App app;

    TechnologyExtended technology;

    @Inject
    TechnologyService technologyService;

    ImageCache imageCache = new ImageCache();

    ObservableList<TechnologyExtended> description = FXCollections.observableArrayList();

    Provider<TechnologyCategoryDescriptionSubComponent> provider = TechnologyCategoryDescriptionSubComponent::new;

    @Inject
    public TechnologyCategorySubComponent() {
    }

    @Override
    public void setItem(@NotNull TechnologyExtended technologyExtended) {
        // TODO: REMOVE
        this.technology = technologyExtended;
        technologyLabel.setText(technologyExtended.id());
        int i = technologyExtended.tags().length;

        if (i > 0 && technologyExtended.tags()[0] != null) {
            tagImage1.setImage(imageCache.get("assets/technologies/tags/" + technologyExtended.tags()[0] + ".png"));
        }
        if (i > 1 && technologyExtended.tags()[1] != null) {
            tagImage2.setImage(imageCache.get("assets/technologies/tags/" + technologyExtended.tags()[1] + ".png"));
        }
        if (i > 2 && technologyExtended.tags()[2] != null) {
            tagImage3.setImage(imageCache.get("assets/technologies/tags/" + technologyExtended.tags()[2] + ".png"));
        }

        researchLabel.setText(String.valueOf(technologyExtended.cost()));

    }

    @OnInit
    public void init() {

    }

    @OnRender
    public void render() {

        timeImage.setImage(imageCache.get("icons/time.png"));
        researchImage.setImage(imageCache.get("icons/resources/research.png"));
//        for (ImageView image : tagImages) {
//            if (technology != null && technology.tags().length > tagImages.indexOf(image)) {
//                image.setImage(imageCache.get("assets/tags/" + Constants.technologyTranslation.get(technology.tags()[tagImages.indexOf(image)])));
//            } else {
//                image.setImage(imageCache.get("test/847.png"));
//            }
//        }

        if (technologyService.getUnlockedTechnologies().contains(technology)) {
            // TODO: Only set description with effect
            // TODO: Delete researchButton, researchLabel, researchImage, timeLabel and timeImage
            researchHBox.getChildren().removeAll();
        } else if (technologyService.getResearchTechnologies().contains(technology)) {
            // TODO: Set description for effect and costs
            // TODO: Set researchButton, researchLabel, researchImage, timeLabel and timeImage
        }

//        descriptionListView.setItems(description);
//        descriptionListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.provider));
    }

    @OnDestroy
    public void destroy() {

    }
}
