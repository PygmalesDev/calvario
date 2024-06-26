package de.uniks.stp24.component.menu;

import de.uniks.stp24.controllers.GangCreationController;
import de.uniks.stp24.model.Trait;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "Trait.fxml")
public class TraitComponent extends HBox implements ReusableItemComponent<Trait> {
    @FXML
    Button chooseTraitButton;
    @FXML
    Button unChooseTraitButton;
    @FXML
    Label traitName;
    @FXML
    Label costText;
    Trait trait;

    GangCreationController gangCreationController;

    Boolean showChoose;
    Boolean showRemove;

    @Inject
    public TraitComponent(GangCreationController gangCreationController, Boolean showChoose, Boolean showRemove) {
        this.gangCreationController = gangCreationController;
        this.showChoose = showChoose;
        this.showRemove = showRemove;
    }

    @Override
    public void setItem(@NotNull Trait trait) {
        this.trait = trait;
        traitName.setText(this.trait.id());
        costText.setText(String.valueOf(trait.cost()));
        setButtonsVisibility(showChoose, showRemove);
    }

    public void chooseTrait() {
        this.gangCreationController.addTrait(trait);
    }

    public void unChooseTrait() {
        this.gangCreationController.deleteTrait(trait);
    }

    public void setButtonsVisibility(boolean showChoose, boolean showRemove) {
        chooseTraitButton.setVisible(showChoose);
        unChooseTraitButton.setVisible(showRemove);
    }

    public void showDetails() {
        // TODO change
        System.out.println(trait.id());
    }
}