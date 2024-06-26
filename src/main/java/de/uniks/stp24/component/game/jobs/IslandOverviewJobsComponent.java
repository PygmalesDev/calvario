package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "IslandOverviewJobs.fxml")
public class IslandOverviewJobsComponent extends AnchorPane {
    @FXML
    public Text noJobText;
    @Inject
    IslandAttributeStorage islandAttributes;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    IslandOverviewJobsComponent() {

    }


    public void insertIslandName() {
        this.noJobText.setText(this.noJobText.getText()
                .replace("{ISLAND_NAME}", this.islandAttributes.getIslandNameTranslated()));
    }
}
