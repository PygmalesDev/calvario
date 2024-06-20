package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.Resource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.util.List;

@Component(view = "DeleteStructureWarning.fxml")
public class DeleteStructureComponent extends VBox{
    @FXML
    Button confirmButton;
    @FXML
    Button cancelButton;
    @FXML
    GridView<String> gridView;
    @FXML
    Text warningText;
    @FXML
    VBox warningContainer;

    public ObservableList<String> items = FXCollections.observableArrayList();
    @Inject
    public DeleteStructureComponent(){

    }

    @OnRender
    public void render() {

        System.out.println(items);

    }

    public void setElements(List<Resource> elements){

    }

    public void setWarningText(String text){

    }

    public void onCancel(){
        setVisible(false);
    }

    public void delete(){

    }
}


