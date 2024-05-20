package de.uniks.stp24.service;

import de.uniks.stp24.App;
import de.uniks.stp24.component.WarningComponent;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.fulib.fx.annotation.controller.SubComponent;

import javax.inject.Inject;

public class PopupBuilder {
    @Inject
    App app;

    @SubComponent
    @Inject
    WarningComponent warningComponent;
    @Inject
    public PopupBuilder(){

    }

    public void showPopup(StackPane container) {
        if (container.getChildren().isEmpty()){
            container.getChildren().add(warningComponent);
            StackPane.setAlignment(warningComponent, Pos.CENTER);
        } else {
            container.setVisible(true);
        }
    }

    public void showWarningPopup(StackPane container) {
        showPopup(container);
        /*container.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                removeBlurCallback.run();
            }
        });*/
    }
}
