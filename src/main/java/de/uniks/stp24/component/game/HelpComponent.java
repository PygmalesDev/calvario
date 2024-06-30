package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Technology;
import de.uniks.stp24.service.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.util.Map;

@Component(view = "help.fxml")
public class HelpComponent extends AnchorPane {

    @FXML
    Button closeButton;
    @FXML
    Button backButton;
    @FXML
    public ListView<Technology> technologyTagsListView;
    private InGameController inGameController;

    public ObservableList<Technology> technologies = FXCollections.observableArrayList();


    @Inject
    public HelpComponent(){

    }

    public void back(){
        setVisible(false);
        inGameController.pauseGameFromHelp();
    }

    public void close(){
        setVisible(false);
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void displayTechnologies() {


        for (Map.Entry<String, String> icon : Constants.technologyIconMap.entrySet()) {
            Technology technology = new Technology(icon.getValue(), icon.getKey());
            technologies.add(technology);
        }

        technologyTagsListView.setItems(technologies);
        technologyTagsListView.setCellFactory(listView -> new TechnologyListCell());
    }
}

class TechnologyListCell extends ListCell<Technology> {
    private final HBox content;
    private final ImageView imageView;
    private final Text text;

    public TechnologyListCell() {
        super();
        imageView = new ImageView();
        text = new Text();
        text.getStyleClass().add("technologyText");

        content = new HBox(imageView, text);
        content.setSpacing(50);
    }

    @Override
    protected void updateItem(Technology technology, boolean empty) {
        super.updateItem(technology, empty);
        if (technology != null && !empty) {
            imageView.setImage(new Image(technology.imageID()));
            text.setText(technology.description());
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }


}
