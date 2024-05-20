package de.uniks.stp24.service;
import de.uniks.stp24.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;

public class PopupBuilder {
    private String fxmlFile;
    private String title;
    private Stage popupStage;

    @Inject
    App app;
    @Inject
    public PopupBuilder(){

    }

    public PopupBuilder(String fxmlFile, String title) {
        this.fxmlFile = fxmlFile;
        this.title = title;
    }
    public void showPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            popupStage = new Stage();
            popupStage.setTitle(title);
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(app.stage());
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
