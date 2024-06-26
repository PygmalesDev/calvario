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
    Trait trait;

    GangCreationController gangCreationController;

    @Inject
    public TraitComponent(GangCreationController gangCreationController) {
        this.gangCreationController =gangCreationController;
    }

    @Override
    public void setItem(@NotNull Trait trait) {
        this.trait = trait;
        traitName.setText(this.trait.id());
    }

    public void chooseTrait() {
        gangCreationController.addTrait(trait);
    }

    public void unChooseTrait() {
        gangCreationController.deleteTrait(trait);
    }
}