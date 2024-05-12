package de.uniks.stp24;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.InGameService;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class PauseMenuTest extends ControllerTest {
    Game game = new Game();

    @Mock
    InGameService inGameService;

    @InjectMocks
    InGameController inGameController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.app.show(this.inGameController);
    }

    @Test
    public void testPausing() {
        doReturn(game).when(this.inGameService).getGame();
    }
}