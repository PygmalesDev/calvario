package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "TechnologyCategoryDescription.fxml")
public class TechnologyCategoryDescriptionSubComponent extends VBox implements ReusableItemComponent<TechnologyExtended> {

    @FXML
    public ImageView resourceImage;
    @FXML
    public Label descriptionLabel;

    TechnologyExtended technology;

    @Inject
    TechnologyService technologyService;

    ImageCache imageCache = new ImageCache();

    @Inject
    Subscriber subscriber;

    @Inject
    public TechnologyCategoryDescriptionSubComponent() {

    }

    @Override
    public void setItem(@NotNull TechnologyExtended technologyExtended) {
        this.technology = technologyExtended;

    }

    @OnRender
    public void render() {

        // TODO: REMOVE
        System.out.println("In TechnologyCategoryDescriptionSubComponent render() method");

        subscriber.subscribe(technologyService.getTechnology(technology.id()),
                technologyExtended -> {
                    technology = technologyExtended;
                },
                error -> System.out.println("Error on getting technology" + technology.id() + ": " + error.getMessage())
        );

        resourceImage.setImage(imageCache.get("assets/tags/" + Constants.technologyTranslation.get(technology.id())));
        descriptionLabel.setText("Description: ");
    }

    @OnInit
    public void init() {

    }

    @OnDestroy
    public void destroy() {
        if (subscriber != null) {
            subscriber.dispose();
        }

    }
}
