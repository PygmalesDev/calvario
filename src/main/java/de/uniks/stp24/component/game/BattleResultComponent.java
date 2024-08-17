package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.helper.BattleEntry;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ContactsService;
import de.uniks.stp24.service.game.IslandsService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "BattleResult.fxml")
public class BattleResultComponent extends Pane {
    public ImageView resultImageView;
    public Button closeResultButton;
    public TextFlow resultTextFlow;
    public Text messageText;
    public Text coloredText;
    public VBox theyLostBox;
    public VBox youLostBox;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    IslandsService islandsService;
    @Inject
    ImageCache imageCache;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    @Inject
    public BattleResultComponent() {}

    public void setInfo(BattleEntry battleEntry) {
        this.setVisible(true);

        if (battleEntry.getWinner().equals(this.tokenStorage.getEmpireId())) {
            this.messageText.setText("Congratulations! Your pirates just won a battle against the ");
            this.coloredText.setText(this.islandsService.getEmpire(battleEntry.getLoser()).name());
            this.resultImageView.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/battle_won.png"));
        } else {
            this.messageText.setText("A black day it be on the high seas. You just lost a battle against ");
            this.coloredText.setText(this.islandsService.getEmpire(battleEntry.getWinner()).name());
            this.resultImageView.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/battle_lost.png"));
        }

        if (battleEntry.getAttacker().equals(this.tokenStorage.getEmpireId())) {
            battleEntry.getShipsLostByAttacker().forEach((type, count) ->
                    this.youLostBox.getChildren().add(new Label(
                            count+"x "+gameResourceBundle.getString("ship." + type))));
            battleEntry.getShipsLostByDefender().forEach((type, count) ->
                    this.theyLostBox.getChildren().add(new Label(
                            count+"x "+gameResourceBundle.getString("ship." + type))));
        } else {
            battleEntry.getShipsLostByDefender().forEach((type, count) ->
                    this.youLostBox.getChildren().add(new Label(
                            count+"x"+gameResourceBundle.getString("ship." + type))));
            battleEntry.getShipsLostByAttacker().forEach((type, count) ->
                    this.theyLostBox.getChildren().add(new Label(
                            count+"x"+gameResourceBundle.getString("ship." + type))));
        }
    }

    public void close() {
        this.setVisible(false);
    }
}
