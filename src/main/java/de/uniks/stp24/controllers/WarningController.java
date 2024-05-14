package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

public class WarningController {
    @FXML
    Button confirmButton;
    @FXML
    Button cancelButton;
    @FXML
    Text gameName;
    @Inject
    App app;
    Stage popupStage = new Stage();

    @Inject
    public WarningController() {
    }

    public void deleteGame() {
    }

    public void onCancel() {
        popupStage.close();
    }

    public void showPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Warning.fxml"));
            Region background = new Region();
            background.setStyle("-fx-background-color: rgba(189, 195, 199, 1);");

            Parent popupContent = loader.load();
            StackPane popupPane = new StackPane(background, popupContent);

            popupStage.initOwner(app.stage());
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(new Scene(popupPane));
            BoxBlur blurEffect = new BoxBlur(5, 5, 3);
            app.stage().getScene().getRoot().setEffect(blurEffect);
            popupStage.showAndWait();
            app.stage().getScene().getRoot().setEffect(null);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
