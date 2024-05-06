package de.uniks.stp24.component;

import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "EnterGame.fxml")
public class EnterGameComponent extends Pane {
    public boolean joinLobbyBoolean;
    @Inject
    public EnterGameComponent() {
        this.joinLobbyBoolean = false;
    }

    public void cancel() {
        System.out.println("Canceled");
    }

    public void joinGame() {
        this.joinLobbyBoolean = true;
        System.out.println("Joined");
    }


}
