package de.uniks.stp24.component;

import de.uniks.stp24.App;
import de.uniks.stp24.service.EditAccService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Component(view = "WarningScreen.fxml")
public class WarningScreenComponent extends VBox {

    @FXML
    VBox warningContainer;

    @Inject
    App app;

    @Inject
    EditAccService editAccService;

    @Inject
    public WarningScreenComponent() {}

    @OnRender
    public void setBackground(){
        warningContainer.setStyle("-fx-background-color: white;");
    }
    public void cancelDelete(ActionEvent actionEvent) {
    }

    public void deleteAcc(ActionEvent actionEvent) {

    }
}
