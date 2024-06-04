package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.Resource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.controlsfx.control.GridView;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

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

    @Inject
    public SitePropertiesComponent(){

    }

    public void onClose(){
        setVisible(false);
    }

    public void buildSite(){

    }

    public void destroySite(){

    }
}
