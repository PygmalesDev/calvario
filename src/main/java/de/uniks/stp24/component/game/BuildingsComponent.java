package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "Buildings.fxml")
public class BuildingsComponent extends AnchorPane {

    @FXML
    public Button prev;
    @FXML
    public Button next;
    @FXML
    public GridPane buildings;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    public IslandAttributeStorage islandAttributeStorage;
    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    private int currentPage = 0;
    private InGameController inGameController;

    @Inject
    public BuildingsComponent() {
    }

    /*

    This method creates and updates the pages for showing all buildings of an island.
    If you click on next or prev the page that should appear will be updated with
    the buildings on next or prev page.

     */
    public void setGridPane() {
        buildings.getChildren().clear();

        prev.setVisible(currentPage > 0);

        int pageCapacity = 8;
        next.setVisible(islandAttributes.getIsland().buildings().size() >= (currentPage + 1) * pageCapacity);

        int row = 0;
        int col = 0;

        for (int i = currentPage * pageCapacity; i < islandAttributes.getIsland().buildings().size(); i++) {
            Building building = new Building(this, islandAttributes.getIsland().buildings().get(i), tokenStorage, islandAttributes, inGameController);
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
            buildings.add(new Building(this, "buildNewBuilding", tokenStorage, islandAttributes, inGameController), col, row);
        } else {
            next.setMouseTransparent(false);
            next.toFront();
            next.setVisible(true);
        }
    }

    /*

    Checks if page on current page is full.

     */
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
            buildings.add(new Building(this, "buildNewBuilding", tokenStorage, islandAttributes, inGameController), 0, 0);
            prev.setVisible(true);
            next.setVisible(false);
        }
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }

}
