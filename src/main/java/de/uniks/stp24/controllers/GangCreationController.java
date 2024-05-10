package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnInit;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

@Title("Gang Creation")
@Controller
public class GangCreationController {

    @Inject
    App app;

    @FXML
    Pane creationPane;
    @FXML
    ImageView flagImage;
    @FXML
    ImageView portraitImage;

    Random rand = new Random();
    ArrayList<File> flagsList = new ArrayList<>();
    ArrayList<File> portraitsList = new ArrayList<>();
    int flagImageIndex = 0;
    int portraitImageIndex = 0;

    @Inject
    public GangCreationController() {

    }

    @OnInit
    public void init() {
        File flagsDir = new File("src/main/resources/de/uniks/stp24/assets/placeholders/Flags");
        for (File flag : flagsDir.listFiles()) {
            flagsList.add(flag);
        }

        File portraitsDir = new File("src/main/resources/de/uniks/stp24/assets/placeholders/Portraits");
        for (File portrait : portraitsDir.listFiles()) {
            portraitsList.add(portrait);
        }
        System.out.println(portraitsList);
    }


    public void back() {

    }

    public void showCreation() {
        creationPane.setVisible(true);
        flagImage.setImage(new Image(flagsList.get(flagImageIndex).toURI().toString()));
        portraitImage.setImage(new Image(portraitsList.get(portraitImageIndex).toURI().toString()));
    }

    public void create() {

    }

    public void showLastFlag() {
        flagImageIndex = flagImageIndex - 1 >= 0 ? flagImageIndex - 1 : flagsList.size() - 1;
        flagImage.setImage(new Image(flagsList.get(flagImageIndex).toURI().toString()));
    }

    public void showNextFlag() {
        flagImageIndex = flagImageIndex + 1 < flagsList.size() ? flagImageIndex + 1 : 0;
        flagImage.setImage(new Image(flagsList.get(flagImageIndex).toURI().toString()));
    }

    public void showLastPortrait() {
        portraitImageIndex = portraitImageIndex - 1 >= 0 ? portraitImageIndex - 1 : portraitsList.size() - 1;
        portraitImage.setImage(new Image(portraitsList.get(portraitImageIndex).toURI().toString()));
    }

    public void showNextPortrait() {
        portraitImageIndex = portraitImageIndex + 1 < portraitsList.size() ? portraitImageIndex + 1 : 0;
        portraitImage.setImage(new Image(portraitsList.get(portraitImageIndex).toURI().toString()));
    }

    public void randomize() {
        flagImageIndex = rand.nextInt(0, flagsList.size() );
        flagImage.setImage(new Image(flagsList.get(flagImageIndex).toURI().toString()));
        portraitImageIndex = rand.nextInt(0, portraitsList.size() );
        portraitImage.setImage(new Image(portraitsList.get(portraitImageIndex).toURI().toString()));
    }
}
