package de.uniks.stp24.service;

import de.uniks.stp24.App;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;

public class PopupBuilder {
    @Inject
    App app;

    Node screenOneToBlur;
    Node screenTwoToBlur;

    @Inject
    public PopupBuilder(){

    }

    public void showPopup(StackPane container, Node component) {
        if (container.getChildren().isEmpty()){
            container.getChildren().add(component);
            container.setVisible(true);
            StackPane.setAlignment(component, Pos.CENTER);
        } else {
            component.setVisible(true);
            container.setVisible(true);
        }

       component.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                removeBlur();
                container.setMouseTransparent(true);
            } else {
                container.setMouseTransparent(false);
            }
        });

        container.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                removeBlur();
                container.setMouseTransparent(true);
            } else {
                container.setMouseTransparent(false);
            }
        });
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
}
