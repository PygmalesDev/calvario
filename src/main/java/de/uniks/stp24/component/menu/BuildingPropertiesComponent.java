package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.ResourcesService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import net.bytebuddy.description.ByteCodeElement;
import org.controlsfx.control.GridView;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

@Component(view = "BuildingProperties.fxml")
public class BuildingPropertiesComponent extends AnchorPane {

    @FXML
    Button closeButton;
    @FXML
    Button destroyButton;
    @FXML
    StackPane resourceContainerBottom;
    @FXML
    StackPane resourceContainerTop;
    @FXML
    GridView<Resource> buildingPropertiesGridView;
    @FXML
    Text buildingName;
    @FXML
    ImageView buildingImage;

    @Inject
    ResourcesService resourcesService;

    @Inject
    Subscriber subscriber;

    @SubComponent
    @Inject
    public LobbyHostSettingsComponent lobbyHostSettingsComponent;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    public BuildingPropertiesComponent(){

    }

    public void destroy(){
        subscriber.subscribe(resourcesService.destroyBuilding(), result -> {
            onClose();
        });
    }

    public void onClose(){
        setVisible(false);
    }
}
