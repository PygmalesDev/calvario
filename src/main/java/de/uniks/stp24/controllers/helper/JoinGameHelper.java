package de.uniks.stp24.controllers.helper;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.BasicController;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.menu.BrowseGameService;
import de.uniks.stp24.service.menu.LobbyService;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Map;

public class JoinGameHelper extends BasicController {

    @Inject
    App app;
    @Inject
    public Subscriber subscriber;
    @Inject
    public EmpireService empireService;
    @Inject
    LobbyService lobbyService;
    @Inject
    BrowseGameService browseGameService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public IslandsService islandsService;

    @Inject
    JobsService jobsService;

    @Inject
    public JoinGameHelper() {
    }

    // I didn't delete this class cause most tests would fail
    public void joinGame(String gameId, boolean sleep) {
            app.show("/loading-screen",
                    Map.of("gameID", gameId,
                            "sleep", sleep));
    }
}