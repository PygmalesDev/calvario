package de.uniks.stp24.component;


import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;


@Component(view = "LobbySettings.fxml")
public class LobbySettingsComponent extends Pane {
    @Inject
    public LobbySettingsComponent() {

    }

    public void selectEmpire() {
        System.out.println("Select Empire");
    }

    public void ready() {
        System.out.println("Ready");
    }

    public void leaveLobby() {
        System.out.println("Left Lobby");
    }
}
