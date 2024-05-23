package de.uniks.stp24.service;

import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.event.OnDestroy;

import javax.inject.Inject;

public class PopupBuilder {
    Node screenOneToBlur;
    Node screenTwoToBlur;
    Pane container = new Pane();

    Node component;

    @Inject
    public PopupBuilder(){

    }

    public void showPopup(Pane container, Node component) {
        this.container = container;
        this.component = component;
        if (container.getChildren().isEmpty()){
            container.getChildren().add(component);
            container.setVisible(true);
            StackPane.setAlignment(component, Pos.CENTER);
        } else {
            component.setVisible(true);
            container.setVisible(true);
        }

        component.visibleProperty().addListener(this::listenVisibilityComponent);
        container.visibleProperty().addListener(this::listenVisibilityContainer);
    }

    public void setBlur(Node screenToBlur, Node screenTwoToBlur){
            this.screenOneToBlur = screenToBlur;

            BoxBlur blur = new BoxBlur(10, 10, 3);
            this.screenOneToBlur.setEffect(blur);
            this.screenOneToBlur.setMouseTransparent(true);
            if (screenTwoToBlur != null){
                this.screenTwoToBlur = screenTwoToBlur;
                this.screenTwoToBlur.setEffect(blur);
                this.screenTwoToBlur.setMouseTransparent(true);
            }


    }

    public void removeBlur(){
        screenOneToBlur.setEffect(null);
        screenOneToBlur.setMouseTransparent(false);
        if (screenTwoToBlur != null) {
            screenTwoToBlur.setEffect(null);
            screenTwoToBlur.setMouseTransparent(false);
        }
    }

    private void listenVisibilityContainer(Observable observable, Boolean oldValue, Boolean newValue){
        if (!newValue) {
            removeBlur();
            container.setMouseTransparent(true);
        } else {
            container.setMouseTransparent(false);
        }
    }

    private void listenVisibilityComponent(Observable observable, Boolean oldValue, Boolean newValue){
        if (!newValue) {
            removeBlur();
            component.setMouseTransparent(true);
        } else {
            component.setMouseTransparent(false);
        }
    }

    @OnDestroy
    public void destroy(){
        container.visibleProperty().removeListener(this::listenVisibilityContainer);
        component.visibleProperty().removeListener(this::listenVisibilityContainer);
    }
}
