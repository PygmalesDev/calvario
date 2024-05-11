package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Gang;
import de.uniks.stp24.component.GangComponent;
import de.uniks.stp24.service.SaveLoadService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

@Title("Gang Creation")
@Controller
public class GangCreationController {
    @Inject
    App app;

    @Inject
    SaveLoadService saveLoadService;

    @Inject
    Provider<GangComponent> gangComponentProvider;

    @FXML
    ListView<Gang> gangsListView;
    @FXML
    Pane creationPane;
    @FXML
    ImageView flagImage;
    @FXML
    ImageView portraitImage;
    @FXML
    ColorPicker colorPicker;
    @FXML
    TextField gangNameText;

    Boolean lockFlag = false;
    Boolean lockPortrait = false;

    Random rand = new Random();
    ArrayList<File> flagsList = new ArrayList<>();
    ArrayList<File> portraitsList = new ArrayList<>();
    int flagImageIndex = 0;
    int portraitImageIndex = 0;

    private ObservableList<Gang> gangs;


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

        gangs = saveLoadService.loadGangs();
    }

    @OnRender
    public void render() {
        creationPane.setVisible(false);
        this.gangsListView.setItems(this.gangs);
        this.gangsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.gangComponentProvider));

    }

    public void back() {

    }

    public void showCreation() {
        creationPane.setVisible(true);
        flagImage.setImage(new Image(flagsList.get(flagImageIndex).toURI().toString()));
        portraitImage.setImage(new Image(portraitsList.get(portraitImageIndex).toURI().toString()));
    }

    public void create() {
        String gangName = gangNameText.getText();
        if (gangNameText.getText().isEmpty()) gangName = "Buccaneers";
        Gang gang = new Gang(gangName, flagsList.get(flagImageIndex).toURI().toString(), portraitsList.get(portraitImageIndex).toURI().toString(), colorPicker.getValue());
        gangs.add(gang);
        saveLoadService.saveGang(gangs);
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
        if (!lockFlag) {
            flagImageIndex = rand.nextInt(0, flagsList.size());
            flagImage.setImage(new Image(flagsList.get(flagImageIndex).toURI().toString()));
        }

        if (!lockPortrait) {
            portraitImageIndex = rand.nextInt(0, portraitsList.size());
            portraitImage.setImage(new Image(portraitsList.get(portraitImageIndex).toURI().toString()));
        }
    }

    public void lockFlag() {
        lockFlag = !lockFlag;
    }

    public void lockPortrait() {
        lockPortrait = !lockPortrait;
    }

    @OnDestroy
    public void destroy() {

    }
}
