package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.service.JoinGameService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "EnterGame.fxml")
public class EnterGameComponent extends AnchorPane {
    @FXML
    TextField passwordInputField;
    @FXML
    public Text errorMessage;
    @Inject
    App app;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @Inject
    @Resource
    ResourceBundle resource;

    protected String gameID;

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    @Inject
    public EnterGameComponent() {
    }

    @Inject
    JoinGameService joinGameService;

    @OnRender
    public void render() {
        this.errorMessage.setVisible(false);
        this.errorMessage.setStyle("-fx-font-size: 0");
    }

    public void cancel() {
        this.app.show("/browseGames");
    }

    public void joinGame() {
        if (!this.getPassword().isEmpty())
            this.subscriber.subscribe(this.joinGameService.joinGame(this.gameID,
                            this.tokenStorage.getUserId(), this.getPassword()),
                    result -> {},
                    error -> this.errorMessage.setText(this.resource.getString("pirate.enterGame.wrongPassword")
                            .replace("{password}", this.getPassword())));
        else
            this.errorMessage.setText(this.resource.getString("pirate.enterGame.noPassword"));
    }

    private String getPassword() {
        return this.passwordInputField.getText();
    }
}
