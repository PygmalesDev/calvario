package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

@Component(view = "Technologies.fxml")
public class TechnologiesComponent extends AnchorPane {

    @FXML
    Button closeButton;
    @FXML
    Button crewRelationsButton;
    @FXML
    Button shipbuildingButton;
    @FXML
    Button marineSienceButton;

    private Pane parent;
    @Inject
    Subscriber subscriber;

    @Inject
    public TechnologiesComponent() {

    }

    @OnInit
    public void init() {

    }

    @OnRender
    public void render() {

    }

    @OnDestroy
    public void destroy() {
        if (subscriber != null) {
            subscriber.dispose();
        }
    }

    public void close() {
        parent.setVisible(false);
    }

    public void shipbuilding() {

    }

    public void crewRelations() {

    }

    public void marineSience() {

    }

    public void setContainer(Pane parent) {
        this.parent = parent;
    }
}
