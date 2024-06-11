package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.ResourcesService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Map;
import java.util.function.ToLongBiFunction;

@Component(view = "SiteProperties.fxml")
public class SitePropertiesComponent extends AnchorPane {
    @FXML
    ListView<Resource> siteProducesListView;
    @FXML
    ListView<Resource> siteConsumesListView;
    @FXML
    GridView<Map<String, Integer>> siteCostsGridView;
    @FXML
    Button buildSiteButton;
    @FXML
    Button destroySiteButton;
    @FXML
    GridView<Resource> siteAmountGridView;
    @FXML
    ImageView siteImage;
    @FXML
    Button closeWindowButton;
    @FXML
    Text siteName;

    String siteType;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    Subscriber subscriber;

    @Inject
    ResourcesService resourcesService;

    @Inject
    IslandsService islandsService;


    @Inject
    public SitePropertiesComponent(){

    }
    private Island island;

    public ObservableList<Map<String, Integer>> resources = FXCollections.observableArrayList();

    @OnRender
    public void render(){
        this.siteType = "energy";
        siteName.setText(siteType);
    }

    public void setSiteType(String siteType){
        this.siteType = siteType;
    }

    public void onClose(){
        setVisible(false);
    }

    public void buildSite(){
        island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.buildSite(tokenStorage.getGameId(), island, siteType), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
        });
    }

    public void destroySite(){
        if (tokenStorage.getIsland().sites().get(siteType) != 0){
            island = tokenStorage.getIsland();
            subscriber.subscribe(resourcesService.destroySite(tokenStorage.getGameId(), island, siteType), result -> {
                tokenStorage.setIsland(islandsService.updateIsland(result));
            });
        }
    }
    @OnRender
    public void displayCostsOfSite(){
        subscriber.subscribe(resourcesService.getResourcesSite(siteType), result -> {
            this.siteCostsGridView.setCellFactory(gridViewCells -> new CustomGridCell());
            System.out.println(result.cost());
        });
    }
}

class CustomGridCell extends GridCell<Map<String, Integer>> {
    @Override
    protected void updateItem(Map<String, Integer> item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.toString());
            setTextAlignment(TextAlignment.CENTER);

        }
    }
}
