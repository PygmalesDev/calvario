package de.uniks.stp24.controllers;

import com.fasterxml.jackson.databind.util.JSONPObject;
import de.uniks.stp24.App;
import de.uniks.stp24.model.Gang;
import de.uniks.stp24.component.GangComponent;
import de.uniks.stp24.service.SaveLoadService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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

import static de.uniks.stp24.service.Constants.empireTemplates;

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
    @FXML
    TextArea gangDescriptionText;
    @FXML
    Button createButton;
    @FXML
    Button editButton;
    @FXML
    Button showDeletePaneButton;
    @FXML
    Text toBeDeletedGangName;
    @FXML
    Pane deletePane;

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
        deletePane.setVisible(false);
        editButton.setVisible(false);
        showDeletePaneButton.setVisible(false);
        this.gangsListView.setItems(this.gangs);
        this.gangsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.gangComponentProvider));
        gangsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Gang gang = gangsListView.getSelectionModel().getSelectedItem();
                if (gang != null) {
                    creationPane.setVisible(true);
                    gangNameText.setText(gang.name());
                    flagImageIndex = gang.flagIndex();
                    flagImage.setImage(new Image(flagsList.get(flagImageIndex).toURI().toString()));
                    portraitImageIndex = gang.portraitIndex();
                    portraitImage.setImage(new Image(portraitsList.get(portraitImageIndex).toURI().toString()));
                    gangDescriptionText.setText(gang.description());
                    createButton.setVisible(false);
                    editButton.setVisible(true);
                    showDeletePaneButton.setVisible(true);
                    colorPicker.setValue(gang.color());
                }
            }
        });
    }

    public void back() {
        app.show("/lobby");
    }

    public Gang getInputGang() {
        String gangName = gangNameText.getText();
        if (gangNameText.getText().isEmpty()) gangName = "Buccaneers";
        return new Gang(gangName, flagsList.get(flagImageIndex).toURI().toString(), flagImageIndex, portraitsList.get(portraitImageIndex).toURI().toString(), portraitImageIndex, gangDescriptionText.getText(), colorPicker.getValue());
    }

    public void edit() {
        int index = gangsListView.getSelectionModel().getSelectedIndex();
        gangs.remove(index);
        Gang gang = getInputGang();
        System.out.println(gang.toString());
        gangs.add(index, gang);
        saveLoadService.saveGang(gangs);
        resetCreationPane();
    }

    public void delete() {
        int index = gangsListView.getSelectionModel().getSelectedIndex();
        gangs.remove(index);
        saveLoadService.saveGang(gangs);
        cancel();
    }

    public void cancel() {
        deletePane.setVisible(false);
        creationPane.setEffect(null);
    }

    public void showCreationPane() {
        creationPane.setVisible(true);
        flagImageIndex = 0;
        portraitImageIndex = 0;
        flagImage.setImage(new Image(flagsList.get(flagImageIndex).toURI().toString()));
        portraitImage.setImage(new Image(portraitsList.get(portraitImageIndex).toURI().toString()));
        gangNameText.setText("");
        gangDescriptionText.setText("");
        createButton.setVisible(true);
        editButton.setVisible(false);
        showDeletePaneButton.setVisible(false);
    }

    public void showDeletePane() {
        creationPane.setEffect(new BoxBlur());
        deletePane.setVisible(true);
        Gang gang = gangsListView.getSelectionModel().getSelectedItem();
        if (gang.color().getRed() + gang.color().getGreen() + gang.color().getBlue() <= 1)
            deletePane.setStyle("-fx-background-color: #949290");
        else
            deletePane.setStyle("-fx-background-color: #333030");
        toBeDeletedGangName.setText(gang.name());
        toBeDeletedGangName.setFill(gang.color());
    }

    public void create() {
        Gang gang = getInputGang();
        gangs.add(gang);
        saveLoadService.saveGang(gangs);
        resetCreationPane();
    }

    public void resetCreationPane() {
        colorPicker.setValue(Color.BLACK);
        flagImageIndex = 0;
        portraitImageIndex = 0;
        flagImage.setImage(new Image(flagsList.get(flagImageIndex).toURI().toString()));
        portraitImage.setImage(new Image(portraitsList.get(portraitImageIndex).toURI().toString()));
        gangNameText.setText("");
        gangDescriptionText.setText("");
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

        String name = empireTemplates.get("Prefix")[rand.nextInt(0, empireTemplates.get("Prefix").length)]
                + " " + empireTemplates.get("Type")[rand.nextInt(0, empireTemplates.get("Type").length)];
        String secondName = "";
        if (rand.nextInt(0, 4) == 3)
            secondName = " of " + empireTemplates.get("Suffix")[rand.nextInt(0, empireTemplates.get("Suffix").length)] +
                    " " + empireTemplates.get("Definition")[rand.nextInt(0, empireTemplates.get("Definition").length)];
        gangNameText.setText(name + secondName);
        String description = empireTemplates.get("Description")[rand.nextInt(0, empireTemplates.get("Description").length)]
                .replace("{NAME}", name);
        gangDescriptionText.setText(description);
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
