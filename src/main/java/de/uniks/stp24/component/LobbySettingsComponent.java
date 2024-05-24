package de.uniks.stp24.component;


import de.uniks.stp24.App;
import de.uniks.stp24.service.LobbyService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Map;
import java.util.ResourceBundle;


@Component(view = "LobbySettings.fxml")
public class LobbySettingsComponent extends AnchorPane {
    @FXML
    Text gameNameField;
    @FXML
    ImageView readyIconImageView;
    @Inject
    Subscriber subscriber;
    @Inject
    LobbyService lobbyService;
    @Inject
    App app;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    @Resource
    ResourceBundle resource;

    private String gameID;
    public boolean leftLobby = false;
    public Image readyIconBlueImage;
    public Image readyIconGreenImage;

    @Inject
    public LobbySettingsComponent() {

    }

    @OnInit
    public void init(){
        readyIconBlueImage = new Image(getClass().getResource("/de/uniks/stp24/icons/approveBlue.png").toExternalForm());
        readyIconGreenImage = new Image(getClass().getResource("/de/uniks/stp24/icons/approveGreen.png").toExternalForm());
    }


    public void setReadyButton(boolean ready){
        if (!ready) {
            readyIconImageView.setImage(readyIconBlueImage);
        }else{
            readyIconImageView.setImage(readyIconGreenImage);
        }
    }

    public void leaveLobby() {
        this.leftLobby = true;
        this.subscriber.subscribe(this.lobbyService.leaveLobby(this.gameID, this.tokenStorage.getUserId()),
                result -> this.app.show("/browseGames"));
    }

    public void selectEmpire() {
        this.app.show("/creation", Map.of("gameid", this.gameID));
    }
    public void setGameName(String gameName) {
        this.gameNameField.setText(gameName);
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void ready() {
        this.subscriber.subscribe(
                this.lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()), result -> {
                    if (result.ready()) {
                        this.subscriber.subscribe(this.lobbyService
                                .updateMember(this.gameID, this.tokenStorage.getUserId(), false, result.empire()));
                        readyIconImageView.setImage(readyIconBlueImage);
                    } else {
                        this.subscriber.subscribe(this.lobbyService
                                .updateMember(this.gameID, this.tokenStorage.getUserId(), true, result.empire()));
                        readyIconImageView.setImage(readyIconGreenImage);
                    }
                });
    }

    @OnDestroy
    public void destroy(){
        readyIconBlueImage = null;
        readyIconGreenImage = null;
    }

}
