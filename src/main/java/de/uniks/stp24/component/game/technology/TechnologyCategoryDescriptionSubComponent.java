package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.model.Effect;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "TechnologyCategoryDescription.fxml")
public class TechnologyCategoryDescriptionSubComponent extends HBox implements ReusableItemComponent<Effect> {

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

    ResourceBundle variablesResourceBundle;

    @Inject
    public TechnologyCategoryDescriptionSubComponent(ResourceBundle variablesResourceBundle) {
        this.variablesResourceBundle = variablesResourceBundle;
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
        descriptionLabel.setStyle("-fx-opacity: 1");
        setEffect(effect);
        setImage();
        setDescriptionLabel();
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public void setDescriptionLabel() {

        if (effect.base() != 0) {
            descriptionLabel.setText("+" + effect.base() + " " + variablesResourceBundle.getString(effect.variable()));
        }

        if (effect.multiplier() != 1 && effect.multiplier() != 0) {
            descriptionLabel.setText(String.format("%+d", (int) ((effect.multiplier() * 100.0) - 100)) + " % "
                    + variablesResourceBundle.getString(effect.variable()));
        }

        if (effect.base() != 0) {
            descriptionLabel.setText(String.format("%+d", (int) effect.base()) + " " + variablesResourceBundle.getString(effect.variable()));
        }
    }

    public void setImage() {

        String variable = this.effect.variable();

        /*
         * Iterate through all resources and checks if effect variable contains a resource
         */
        for (String key : Constants.resourceTranslation.keySet()) {
            if (variable.contains(key)) {
                resourceImage.setImage(imageCache.get("icons/resources/" + key + ".png"));
                break;
            } else if (variable.contains("pop")) {
                resourceImage.setImage(imageCache.get("icons/resources/population.png"));
                break;
            }
        }

        /*
         * If variable doesn't affect a resource, it must be a technology
         */
        if (resourceImage.getImage() == null) {
            for (String tech : Constants.technologyTranslation.values()) {
                if (variable.contains(tech)) {
                    resourceImage.setImage(imageCache.get("assets/technologies/tags/" + tech + ".png"));
                    break;
                }
            }
        }
    }
}
