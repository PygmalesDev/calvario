package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.ResourcesService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.controlsfx.control.GridView;
import org.fulib.fx.annotation.controller.Component;
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
    GridView<Resource> siteCostsGridView;
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
        island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.destroySite(tokenStorage.getGameId(), island, siteType), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
        });
    }
}
