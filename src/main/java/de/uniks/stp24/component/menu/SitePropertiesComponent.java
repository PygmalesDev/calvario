package de.uniks.stp24.component.menu;

import de.uniks.stp24.component.game.ResourceComponent;
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
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.ToLongBiFunction;

@Component(view = "SiteProperties.fxml")
public class SitePropertiesComponent extends AnchorPane {
    @FXML
    ListView<Resource> siteProducesListView;
    @FXML
    ListView<Resource> siteConsumesListView;
    @FXML
    GridView<ResourceComponent> siteCostsGridView;
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
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;


    @Inject
    public SitePropertiesComponent(){

    }

    private Island island;

    public ObservableList<Map<String, Integer>> resources;
    public ObservableList<ResourceComponent> resourceComponents;



    @OnRender
    public void render(){
        this.siteType = "mining";
        siteName.setText(siteType);
    }

    @FXML
    public void initialize() {
        // Ensure resources list is initialized
        if (this.resourceComponents == null) {
            this.resourceComponents = FXCollections.observableArrayList();
        }
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
            siteCostsGridView.setCellWidth(150);
            for (Map.Entry<String, Integer> entry : result.cost().entrySet()) {
                Resource resource = new Resource(entry.getKey(), entry.getValue(), 0);
                Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, true, true, false, gameResourceBundle);
                ResourceComponent resourceComponent = resourceComponentProvider.get();
                resourceComponent.setItem(resource);
                resourceComponents.add(resourceComponent);
            }
            siteCostsGridView.setItems(resourceComponents);
        });
    }
}

