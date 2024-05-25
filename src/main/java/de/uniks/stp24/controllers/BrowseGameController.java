package de.uniks.stp24.controllers;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.sun.javafx.binding.SelectBinding;
import de.uniks.stp24.App;
import de.uniks.stp24.component.BubbleComponent;
import de.uniks.stp24.component.GameComponent;
import de.uniks.stp24.component.LogoutComponent;
import de.uniks.stp24.component.WarningComponent;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.*;
import de.uniks.stp24.ws.EventListener;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Title("%browse.game")
@Controller
public class
BrowseGameController extends BasicController {
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
    ImageView deleteIconImageView;
    @FXML
    AnchorPane gameListAnchorPane;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;

    @FXML
    TextField searchLine;

    @FXML
    Pane captainContainer;
    @FXML
    StackPane warningWindowContainer;

    @SubComponent
    @Inject
    public WarningComponent warningComponent;
    @SubComponent
    @Inject
    public LogoutComponent logoutComponent;
    @SubComponent
    @Inject
    public BubbleComponent bubbleComponent;

    @Inject
    GamesApiService gamesApiService;
    @Inject
    Provider<GameComponent> gameComponentProvider;
    @Inject
    EventListener eventListener;
    @Inject
    public GameComponent gameComponent;
    @Inject
    public BrowseGameService browseGameService;
    @Inject
    EditGameService editGameService;
    @Inject
    PopupBuilder popupBuilder;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    CreateGameService createGameService;
    PopupBuilder popup = new PopupBuilder();
    PopupBuilder popupLogout = new PopupBuilder();

    @Inject
    public BrowseGameController(){
    }

    // the fxml has no containers (text, label) for errors;
    private
    Text textInfo;

    private ObservableList<Game> games = FXCollections.observableArrayList();
    private BooleanBinding deleteWarningIsInvisible;
    private Image deleteIconRedImage;
    private Image deleteIconBlackImage;

    @OnRender
    void createBindings() {
        gameList.setItems(games);
        gameList.setCellFactory(list -> new ComponentListCell<>(app, gameComponentProvider));
        this.searchLine.textProperty().addListener((observable, oldValue, newValue) -> {
            this.gameList.scrollTo(0);
            if (newValue.isEmpty()) sortNewGamesOnTop();
            else this.games.sort(Comparator.comparing(game -> !game.name().toLowerCase().contains(newValue.toLowerCase())));
        });
    }

    private void sortNewGamesOnTop() {
        this.games.sort(Comparator.comparing(Game::createdAt).reversed());
        this.games.sort(Comparator.comparing(game -> !game.owner().equals(this.tokenStorage.getUserId())));
    }



    //Load list of games as soon as BrowseGame-Screen is shown
    @OnInit
    void init() {
        deleteIconRedImage = new Image(getClass().getResource("/de/uniks/stp24/icons/deleteRed.png").toExternalForm());
        deleteIconBlackImage = new Image(getClass().getResource("/de/uniks/stp24/icons/deleteBlack.png").toExternalForm());
        this.controlResponses = responseConstants.respDelGame;
        this.textInfo = new Text("");

        editGameService = (editGameService == null) ? new EditGameService() : editGameService;
        createGameService = (createGameService == null) ? new CreateGameService() : createGameService;
        browseGameService = (browseGameService == null) ? new BrowseGameService() : browseGameService;
        browseGameService.resetSelectedGame();
        subscriber.subscribe(gamesApiService.findAll(),
          gameList -> {
              Platform.runLater(() -> {
                  games.setAll(gameList);
                  editGameService.setGamesList(games);
                  createGameService.setGamesList(games);
                  this.sortNewGamesOnTop();
              });},
          error -> {
              int code = errorService.getStatus(error);
              this.textInfo.setText(getErrorInfoText(code));
          });

        // Listener for updating list of games if games are created, deleted or updated
        subscriber.subscribe(eventListener.listen("games.*.*", Game.class), event -> {
            Platform.runLater(() -> {
                switch (event.suffix()) {
                    case "created" -> games.add(event.data());
                    case "update" -> games.replaceAll(g -> g._id().equals(event.data()._id()) ? event.data() : g);
                    case "deleted" -> games.removeIf(g -> g._id().equals(event.data()._id()));
                }
                this.sortNewGamesOnTop();
            });},
            error -> {
                int code = errorService.getStatus(error);
                this.textInfo.setText(getErrorInfoText(code));
            });

    }

    @OnRender
    public void addSpeechBubble() {
        captainContainer.getChildren().add(bubbleComponent);
        Platform.runLater(() -> bubbleComponent.setCaptainText(resources.getString("pirate.browseGame.which.game")));
    }

    //Make list of games visible
    @OnRender
    void render() {
        this.deleteWarningIsInvisible = this.warningWindowContainer.visibleProperty().not();
    }

    @OnRender
    public void changeDeleteButtonView(){
        // delete Button has red text and icon when selected and the captain says something different
        this.del_game_b.styleProperty().bind(Bindings.createStringBinding(()->{
            if(deleteWarningIsInvisible.get()) {
                this.deleteIconImageView.setImage(deleteIconBlackImage);
                bubbleComponent.setCaptainText(resources.getString("pirate.browseGame.which.game"));
                return "-fx-text-fill: Black";
            }else {
                this.deleteIconImageView.setImage(deleteIconRedImage);
                bubbleComponent.setCaptainText(resources.getString("pirate.browseGame.whiping.off.the.map"));
                return "-fx-text-fill: #CF2A27";
            }
        },this.deleteWarningIsInvisible));
    }



    /*
    ============================================= On-Action buttons =============================================
     */

    public void logOut() {
        popup.showPopup(logoutWarningContainer, logoutComponent);
        popup.setBlur(gameListAnchorPane, browseGameVBoxButtons);
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
            warningComponent.setView(this.warningWindowContainer);
            popup.showPopup(warningWindowContainer, warningComponent);
            popup.setBlur(gameListAnchorPane, browseGameVBoxButtons);
        } else {
            this.textInfo.setText(getErrorInfoText(403));
        }
    }

    @OnDestroy
    void destroy() {
        subscriber.dispose();
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
        deleteIconBlackImage = null;
        deleteIconRedImage = null;
    }

}
