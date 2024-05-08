package de.uniks.stp24.component;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.service.BrowseGameService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "Game.fxml")
public class GameComponent extends HBox implements ReusableItemComponent <Game>{
    @FXML Text game_name;

    @Inject
    BrowseGameService browseGameService;

    private Game game;

    @Override
    public void setItem(@NotNull Game game) {
        game_name.setText(game.name());
        this.game = game;
    }

    //Check if component is selected
    @Inject
    public GameComponent(){
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                browseGameService.handleGameSelection(game);
            }
        });
    }
}