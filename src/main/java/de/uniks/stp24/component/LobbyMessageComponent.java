package de.uniks.stp24.component;

import de.uniks.stp24.App;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "LobbyMessage.fxml")
public class LobbyMessageComponent extends Pane {
    @Inject
    App app;
    @Inject
    public LobbyMessageComponent() {

    }

    public void goBack() {
        this.app.show("/browsegames");
    }
}
