package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.component.GameComponent;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.CreateGameService;
import de.uniks.stp24.service.EditGameService;
import de.uniks.stp24.ws.EventListener;
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
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Provider;

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
    @Inject
    Provider<GameComponent> gameComponentProvider;
    @Inject
    EventListener eventListener;
    @Inject
    GameComponent gameComponent;
    @Inject
    BrowseGameService browseGameService;
    @Inject
    EditGameService editGameService;
    @Inject
    CreateGameService createGameService;


    private final ObservableList<Game> games = FXCollections.observableArrayList();

    //Load list of games as soon as BrowseGame-Screen is shown
    @OnInit
    void init(){
        subscriber.subscribe(gamesApiService.findAll().subscribe(this.games::setAll));
        editGameService.setGamesList(games);
        createGameService.setGamesList(games);
        subscriber.subscribe(eventListener.listen("games.*.*", Game.class), event -> {
            switch (event.suffix()) {
                case "created" -> games.add(0,event.data());
                case "update" -> games.replaceAll(g -> g._id().equals(event.data()._id()) ? event.data() : g);
                case "deleted" -> games.removeIf(g -> g._id().equals(event.data()._id()));
            }
        });
    }

    //Make list of games visible
    @OnRender
    void render(){
        gameList.setItems(games);
        gameList.setCellFactory(list -> new ComponentListCell<>(app, gameComponentProvider));
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


    //Back to log in Screen after click Logout in BrowseGame Screen
    public void logOut(ActionEvent actionEvent) {
        logOut();
    }

    public void logOut(){
        app.show("/login");
    }

    public void newGame() {
        app.show("/createGameController");
    }

    public void editGame() {


        app.show("/editgame");
    }
}
