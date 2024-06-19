package de.uniks.stp24.component.game;

import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
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
    @Inject
    TokenStorage tokenStorage;
    @Inject
    public IslandAttributeStorage islandAttributeStorage;

    private int currentPage = 0;
    private int pageCapacity = 8;

    @Inject
    public BuildingsComponent() {
    }

    //Call set grid pane if buildings changes
    public void setGridPane() {
        int page = currentPage + 1;
        System.out.println("Set page: " + page);
        buildings.getChildren().clear();

        if (currentPage > 0) {
            prev.setVisible(true);
        } else {
            prev.setVisible(false);
        }

        if (islandAttributes.getIsland().buildings().size() < (currentPage + 1) * pageCapacity) {
            next.setVisible(false);
        } else {
            next.setVisible(true);
        }

        int row = 0;
        int col = 0;

        for (int i = currentPage * pageCapacity; i < islandAttributes.getIsland().buildings().size(); i++) {
            Building building = new Building(this, islandAttributes.getIsland().buildings().get(i), tokenStorage, islandAttributes);
            buildings.add(building, col, row);

            if ((i + 1) % 8 == 0) {
                break;
            }

            col++;
            if (col >= 4) {
                col = 0;
                row++;
                if (row >= 2) {
                    break;
                }
            }
        }

        if (!isGridPaneFull(currentPage)) {
            buildings.add(new Building(this, "empty", tokenStorage, islandAttributes), col, row);
        } else {
            next.setVisible(true);
        }
    }

    public boolean isGridPaneFull(int pageToCheck) {
        if (!islandAttributes.getIsland().buildings().isEmpty()) {
            int size = islandAttributes.getIsland().buildings().size();
            int indexToCheck = (pageToCheck + 1) * 8 - 1;
            return indexToCheck < size;
        }
        return false;
    }

    public void resetPage() {
        prev.setVisible(false);
        next.setVisible(false);
        this.currentPage = 0;
    }

    public void goPrevSite() {
        currentPage = currentPage - 1;
        setGridPane();
    }

    public void goNextSite() {
        int size = islandAttributes.getIsland().buildings().size();
        if (isGridPaneFull(currentPage + 1) || (!isGridPaneFull(currentPage + 1) && size % 8 != 0)) {
            currentPage = currentPage + 1;
            setGridPane();
        } else if(!isGridPaneFull(currentPage + 1) && size % 8 == 0){
            currentPage = currentPage + 1;
            buildings.getChildren().clear();
            buildings.add(new Building(this, "empty", tokenStorage, islandAttributes), 0, 0);
            prev.setVisible(true);
        }
    }
}
