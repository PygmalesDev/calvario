package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.LanguageService;
import de.uniks.stp24.service.PrefService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "Settings.fxml")
public class SettingsComponent extends VBox {

    @FXML
    ToggleButton germanLang;
    @FXML
    ToggleButton englishLang;
    @FXML
    Button backButton;

    @Inject
    App app;
    @Inject
    InGameService inGameService;
    @Inject
    public PrefService prefService;
    @Inject
    LanguageService languageService;

    @Inject
    @Resource
    ResourceBundle resources;


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
