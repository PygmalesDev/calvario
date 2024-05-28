package de.uniks.stp24.component;

import de.uniks.stp24.controllers.GangCreationController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "WarningGangDeletion.fxml")
public class GangDeletionComponent extends VBox {

    @FXML
    Button deleteGangButton;
    @FXML
    Button cancelDeleteButton;
    @FXML
    Text warningText;
    @FXML
    VBox warningContainer;

    @Inject
    @Resource
    ResourceBundle resources;


    @Inject
    public GangDeletionComponent() {

    }

    public void cancelDelete() {
        getParent().setVisible(false);
    }

    public void deleteGang(){
        getParent().setVisible(false);
    }
}
