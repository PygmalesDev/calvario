package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.component.GameComponent;
import de.uniks.stp24.component.LogoutComponent;
import de.uniks.stp24.component.WarningComponent;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.CreateGameService;
import de.uniks.stp24.service.EditGameService;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.ws.EventListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;
import java.util.ResourceBundle;

@Title("Browse Game")
@Controller
public class
BrowseGameController {
    @FXML
    public Button load_game_b;
    @FXML
    public Button new_game_b;
    @FXML
    public Button edit_acc_b;
    @FXML
    public Button del_game_b;
    @FXML
    public Button log_out_b;
    @FXML
    public Button edit_game_b;
    @FXML
    public ListView<Game> gameList;
    @FXML
    public VBox browseGameVBoxButtons;
    @FXML
    public VBox browseGameVBoxList;
    @FXML
    StackPane logoutWarningContainer;

    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;


    @Inject
    App app;
    @FXML
    StackPane warningWindowContainer;
    @SubComponent
    @Inject
    WarningComponent warningComponent;

    @SubComponent
    @Inject
    public LogoutComponent logoutComponent;

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
    public BrowseGameService browseGameService;
    @Inject
    EditGameService editGameService;

    @Inject
    PopupBuilder popupBuilder;
    @Inject
    CreateGameService createGameService;
    @Inject
    @Resource
    ResourceBundle resources;
    PopupBuilder popup = new PopupBuilder();
    PopupBuilder popupLogout = new PopupBuilder();

    private ObservableList<Game> games = FXCollections.observableArrayList();


    //Load list of games as soon as BrowseGame-Screen is shown
    @OnInit
    void init() {


        editGameService = (editGameService == null) ? new EditGameService() : editGameService;
        createGameService = (createGameService == null) ? new CreateGameService() : createGameService;
        browseGameService = (browseGameService == null) ? new BrowseGameService() : browseGameService;


        browseGameService.resetSelectedGame();

        gamesApiService.findAll().subscribe(gameList -> {
            Platform.runLater(() -> {
                games.setAll(gameList);
                editGameService.setGamesList(games);
                createGameService.setGamesList(games);
                // Update the ListView after data is set
                updateListView();
            });
        });

        // Listener for updating list of games if games are created, deleted or updated
        subscriber.subscribe(eventListener.listen("games.*.*", Game.class), event -> {
            Platform.runLater(() -> {
                switch (event.suffix()) {
                    case "created" -> games.add(event.data());
                    case "update" -> games.replaceAll(g -> g._id().equals(event.data()._id()) ? event.data() : g);
                    case "deleted" -> games.removeIf(g -> g._id().equals(event.data()._id()));
                }
            });
        });
    }

    //Make list of games visible
    @OnRender
    void render() {
        updateListView();
    }

    @OnDestroy
    void destroy() {
        subscriber.dispose();
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }

    @Inject
    public BrowseGameController() {
    }

    public void updateListView(){
        games = browseGameService.sortGames(games);
        gameList.setItems(games);
        gameList.setCellFactory(list -> new ComponentListCell<>(app, gameComponentProvider));
    }

    /*
    ============================================= On-Action buttons =============================================
     */
    public void logOut() {
        popupLogout.showPopup(logoutWarningContainer, logoutComponent);
        popupLogout.setBlur(browseGameVBoxList, browseGameVBoxButtons);
    }

    public void newGame() {
        app.show("/createGameController");
    }

    public void editGame() {
        if(browseGameService.checkMyGame()) {
            app.show("/editgame");
        }
    }

    public void editAccount() {
        app.show("/editAcc");
    }

    public void loadGame() {
        if(browseGameService.getGame() != null) {
            app.show("/lobby", Map.of("gameid", browseGameService.getGame()._id()));
        }
    }
    public void deleteGame() {
        if(browseGameService.checkMyGame()) {
            warningComponent.setGameName();
            popup.showPopup(warningWindowContainer, warningComponent);
            popup.setBlur(browseGameVBoxList, browseGameVBoxButtons);
        }
    }
}
