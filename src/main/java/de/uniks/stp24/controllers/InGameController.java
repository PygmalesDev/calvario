package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.scene.input.KeyCode;
import org.fulib.fx.annotation.event.OnKey;

import javax.inject.Inject;

public class InGameController {
    @Inject
    App app;

    @Inject
    PauseController pauseController;

    @OnKey(code = KeyCode.ESCAPE)
    public void pauseGame() {
        app.show("/pause");
    }
}
