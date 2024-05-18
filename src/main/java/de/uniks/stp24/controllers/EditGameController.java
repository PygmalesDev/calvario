package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.EditGameService;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.Title;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Title("Edit Game")
@Controller
public class EditGameController {
    @FXML
    HBox errorBoxEdit;
    @FXML
    Text errorMessageTextEdit;
    @FXML
    Button editGameConfirmButton;
    @FXML
    Button editGameCancelButton;
    @FXML
    TextField editNameTextField;
    @FXML
    TextField editPasswordTextField;
    @FXML
    TextField editRepeatPasswordTextField;
    @FXML
    Spinner<Integer> editMapSizeSpinner;

    @Inject
    App app;
    @Inject
    GamesApiService gamesApiService;
    @Inject
    BrowseGameService browseGameService;
    @Inject
    BrowseGameController browseGameController;
    @Inject
    @Resource
    ResourceBundle resources;

    @Inject
    public EditGameController(){

    }
    @Inject
    EditGameService editGameService;
    @FXML
    public void initialize() {
        editGameService.setEditGameController(this);
        initializeSpinner();
    }

    public void initializeSpinner(){
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000);
        valueFactory.setValue(100);
        editMapSizeSpinner.setValueFactory(valueFactory);
    }

    public void editGame(){
        initializeSpinner();
        GameSettings settings = new GameSettings(this.editMapSizeSpinner.getValue());
        String gameName = this.editNameTextField.getText();
        String password = this.editPasswordTextField.getText();
        if(!gameName.isEmpty() && !password.isEmpty() && !editRepeatPasswordTextField.getText().isEmpty()) {
            if(password.equals(editRepeatPasswordTextField.getText())) {
                if (editGameService.editGame(gameName, settings, password) != null) {
                    editGameService.editGame(gameName, settings, password).subscribe(result -> {
                                Platform.runLater(() -> {
                                    browseGameService.resetSelectedGame();
                                    browseGameController.init();
                                    app.show(browseGameController);
                                });
                            });
                }
            }
        }
    }
    public void cancel(){
        browseGameService.resetSelectedGame();
        app.show("/browseGames");
    }
    public void showNameTakenError() {
        errorMessageTextEdit.setText(resources.getString("name.exists.already"));
        errorBoxEdit.setVisible(true);
    }

    public void hideErrorBox() {
        errorBoxEdit.setVisible(false);
    }
}
