package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;

import javax.inject.Inject;

import java.util.Random;

@Title("CALVARIO")
@Controller
public class LoadingScreenController extends BasicController {

    @FXML
    AnchorPane backGround;
    @FXML
    Pane captainContainer;

    @SubComponent
    @Inject
    public BubbleComponent bubbleComponent;

    @Param("gameID")
    public String gameID;

    @Param("sleep")
    public boolean sleep;

    @Inject
    EmpireService empireService;

    @Inject
    IslandsService islandsService;

    @Inject
    TokenStorage tokenStorage;

    Random random = new Random();

    @Inject
    public LoadingScreenController() {

    }

    @OnRender
    public void render() {
        captainContainer.getChildren().add(bubbleComponent);
        bubbleComponent.setCaptainText("LOADING ...");
        int rand = random.nextInt(1, 8);
        String backgroundImage = "-fx-background-image: url('[PATH]');"
                .replace("[PATH]", "/de/uniks/stp24/assets/backgrounds/loading/l" + rand + ".jpg");
        backGround.setStyle(backgroundImage + "-fx-background-size: cover;");


        // Go through all empires of the game and save the empireId and gameId for the user
        // If there is no empire which belongs to the user, the user is a spectator.
        subscriber.subscribe(empireService.getEmpires(gameID), dto -> {
            for (ReadEmpireDto data : dto) {
                islandsService.saveEmpire(data._id(), data);
                if (data.user().equals(tokenStorage.getUserId()))
                    startGame(gameID, data._id(), false);
            }
            if (tokenStorage.getEmpireId() == null)
                startGame(gameID, null, true);

            islandsService.retrieveIslands(gameID, sleep);
        }, error -> System.out.println(error.getMessage()));
    }


    private void startGame(String gameId, String empireId, boolean isSpectator) {
        this.tokenStorage.setGameId(gameId);
        this.tokenStorage.setEmpireId(empireId);
        this.tokenStorage.setIsSpectator(isSpectator);
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
