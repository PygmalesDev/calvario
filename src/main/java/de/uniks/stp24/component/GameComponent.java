package de.uniks.stp24.component;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.EditGameService;
import de.uniks.stp24.service.TokenStorage;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "Game.fxml")
public class GameComponent extends HBox implements ReusableItemComponent<Game> {
    @FXML
    public Text game_name;

    @Inject
    BrowseGameService browseGameService;

    @Inject
    EditGameService editGameService;

    @Inject
    TokenStorage tokenStorage;

    private Game game;

    @Override
    public void setItem(@NotNull Game game) {
        game_name.setText(game.name());

        if(tokenStorage == null){
            setTestToken();
        }

        //Your game will be displayed in color green
        if (game.owner().equals(tokenStorage.getUserId())) {
            game_name.setFill(Color.GREEN);
        } else {
            game_name.setFill(Color.BLACK);
        }


        this.game = game;
    }

    //Check if component is selected
    @Inject
    public GameComponent() {
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editGameService.setClickedGame(game);
                browseGameService.handleGameSelection(game);
            }
        });
    }

    //Set Token for testing
    private void setTestToken(){
        tokenStorage = new TokenStorage();
        tokenStorage.setName(null);
        tokenStorage.setToken(null);
        tokenStorage.setAvatar(null);
        tokenStorage.setUserId("testID");
    }
}