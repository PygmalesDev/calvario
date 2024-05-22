package de.uniks.stp24.controllers;


import de.uniks.stp24.App;
import de.uniks.stp24.component.BubbleComponent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Title("Licenses")
@Controller
public class LicensesController extends BasicController {

    @FXML
    Button backToLoginButton;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;

    @FXML
    Pane captainContainer;
    @Inject
    App app;
    @Inject
    @Resource
    ResourceBundle resources;

    @SubComponent
    @Inject
    BubbleComponent bubbleComponent;

    @OnRender
    public void addSpeechBubble() {
        captainContainer.getChildren().add(bubbleComponent);
        Platform.runLater(() -> {
            bubbleComponent.setCaptainText(resources.getString("pirate.licenses.text"));
        });
    }

    @Inject
    public LicensesController() {
    }

    public void backToLogin() {
        app.show("/login");
    }

    @OnDestroy
    public void destroy(){
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }
}
