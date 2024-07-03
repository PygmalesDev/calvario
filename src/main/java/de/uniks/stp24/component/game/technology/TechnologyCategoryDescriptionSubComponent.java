package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.model.Effect;
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
public class TechnologyCategoryDescriptionSubComponent extends VBox implements ReusableItemComponent<Effect> {

    @FXML
    public ImageView resourceImage;
    @FXML
    public Label descriptionLabel;

    Effect effect;

    @Inject
    TechnologyService technologyService;

    ImageCache imageCache = new ImageCache();

    @Inject
    Subscriber subscriber;

    @Inject
    public TechnologyCategoryDescriptionSubComponent() {

    }

    @OnRender
    public void render() {

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

    @Override
    public void setItem(@NotNull Effect effect) {
        String variable = effect.variable();
        for (String key : Constants.resourceTranslation.keySet()) {
            if (variable.contains(key)) {
                // TODO: REMOVE THIS
                System.out.println("Image sets");
                resourceImage.setImage(imageCache.get("icons/resources/" + key + ".png"));
            }
        }

        descriptionLabel.setText(effect.variable());
    }
}
