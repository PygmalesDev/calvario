package de.uniks.stp24.component;

import de.uniks.stp24.service.InGameService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "Settings.fxml")
public class SettingsComponent extends VBox {

    @FXML
    ToggleButton germanLang;
    @FXML
    ToggleButton englishLang;
    @FXML
    Button backButton;

    @Inject
    InGameService inGameService;


    @Inject
    public SettingsComponent() {

    }

    public void back() {
        inGameService.setShowSettings(false);
    }

    public void setToGerman() {
        inGameService.setLanguage(0);
    }

    public void setToEnglish() {
        inGameService.setLanguage(1);
    }
}
