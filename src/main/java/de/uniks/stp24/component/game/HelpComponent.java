package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

@Component(view = "help.fxml")
public class HelpComponent extends VBox {

    @FXML
    Button backButton;
    @FXML
    ListView technologyTagsListView;
}
