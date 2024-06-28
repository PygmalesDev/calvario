package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Technology;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "help.fxml")
public class HelpComponent extends AnchorPane {

    @FXML
    Button backButton;
    @FXML
    ListView<Technology> technologyTagsListView;
    private InGameController inGameController;

    @Inject
    public HelpComponent(){

    }

    public void back(){
        setVisible(false);
        inGameController.pauseGame();
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void displayTechnologies() {

    }
}
