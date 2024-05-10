package de.uniks.stp24;

import de.uniks.stp24.controllers.LobbyController;
import de.uniks.stp24.service.JoinGameService;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;

@ExtendWith(MockitoExtension.class)
public class TestJoinLobby extends ControllerTest {
    @Spy
    JoinGameService joinGameService;
    @Spy
    Subscriber subscriber;
    @InjectMocks
    LobbyController lobbyController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.app.show(this.lobbyController);
    }

    @Test
    public void testJoinLobbyAsHost() {

    }
}
