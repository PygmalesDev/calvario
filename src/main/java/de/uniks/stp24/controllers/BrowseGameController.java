package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

@Controller
public class BrowseGameController {
    @FXML Button load_game_b;
    @FXML Button new_game_b;
    @FXML Button edit_acc_b;
    @FXML Button del_game_b;
    @FXML Button log_out_b;
    @FXML
    ListView<Game> gameList;

    @Inject
    App app;
    @Inject
    GamesApiService gamesApiService;
    @Inject
    Subscriber subscriber;


    private final ObservableList<Game> games = FXCollections.observableArrayList();

    //Load list of games as soon as BrowseGame-Screen is shown
    @OnInit
    void init(){
        subscriber.subscribe(gamesApiService.findAll().subscribe(this.games::setAll));
    }

    //Make list of games visible
    @OnRender
    void render(){
        gameList.setItems(games);
    }

    @OnDestroy
    void destroy(){
        subscriber.dispose();
    }


    //TODO: Load all Games from Server and show them on Screen



    /*
    public void loadGames(){
        gamesApiService.games().subscribe(results -> {
            List<Game> gameList = Collections.singletonList(results);
            gameList.forEach(System.out::println);
        });
    }

     */

    @Inject
    public BrowseGameController(){
    }


    //Back to Login Screen after click Logout in BrowseGame Screen
    public void logOut(ActionEvent actionEvent) {
        logOut();
    }

    public void logOut(){
        app.show("/login");
    }
}
