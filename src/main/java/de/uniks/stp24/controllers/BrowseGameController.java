package de.uniks.stp24.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.fulib.fx.annotation.controller.Controller;

import javax.inject.Inject;

@Controller
public class BrowseGameController {
    @FXML
    Button game_1_b;
    @FXML Button game_2_b;
    @FXML Button game_3_b;
    @FXML Button game_4_b;
    @FXML Button game_5_b;
    @FXML Button load_game_b;
    @FXML Button new_game_b;
    @FXML Button edit_acc_b;
    @FXML Button del_game_b;
    @FXML Button log_out_b;


    @Inject
    public BrowseGameController(){

    }
}
