package de.uniks.stp24.component.game;

import de.uniks.stp24.service.IslandAttributeStorage;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component(view = "buildings.fxml")
public class BuildingsComponent extends VBox {

    @FXML
    public Pane prev;
    @FXML
    public Pane next;
    @FXML
    public GridPane buildings;

    @Inject
    public IslandAttributeStorage islandAttributes;

    private int currentPage = 0;

    @Inject
    public BuildingsComponent() {
    }

    //Call set grid pane if buildings changes
    public void setGridPane() {
        buildings.getChildren().clear();

        if(currentPage > 0){
            prev.setVisible(true);
        } else {
            prev.setVisible(false);
        }

        int row = 0;
        int col = 0;

        for (int i = currentPage * 8; i < islandAttributes.getIsland().buildings().length; i++) {
            if((i + 1) % 8 != 0) {
                Building building = new Building(this, islandAttributes.getIsland().buildings()[i]);
                buildings.add(building, col, row);

                col++;
                if (col >= 4) {
                    col = 0;
                    row++;
                    if (row >= 2) {
                        break;
                    }
                }
            }
        }

        if(!isGridPaneFull()){
            buildings.add(new Building(this, "empty"), col, row);
        } else {
            next.setVisible(true);
        }
    }

    public boolean isGridPaneFull() {
        if(islandAttributes.getIsland().buildings().length > 0) {
            return islandAttributes.getIsland().buildings().length % 8 == 0;
        }
        return false;
    }

    public void resetPage(){
        prev.setVisible(false);
        next.setVisible(false);
        this.currentPage = 0;
    }

    public void goPrevSite() {
        currentPage = currentPage - 1;
        setGridPane();
    }

    public void goNextSite() {
        currentPage = currentPage + 1;
        if(isGridPaneFull() && (currentPage - 1) * 8 == islandAttributes.getIsland().buildings().length){
            buildings.getChildren().clear();
            buildings.add(new Building(this,"empty"), 0, 0);
            return;
        }
        setGridPane();
    }
}
