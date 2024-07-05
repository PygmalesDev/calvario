package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "ResearchJob.fxml")
public class ResearchJobComponent extends AnchorPane {

    @FXML
    ImageView technologyTagImage2;
    @FXML
    ImageView technologyTagImage1;
    @FXML
    ListView technologyEffectsListView;
    @FXML
    Text technologyNameText;
    @FXML
    Text researchCostText;
    @FXML
    Button cancelResearchButton;
    @FXML
    Text researchProgressText;
    @FXML
    ProgressBar researchProgressBar;
    @FXML
    AnchorPane researchBackground;
    private TechnologyCategoryComponent technologyCategoryComponent;
    private InGameController inGameController;

    @Inject
    JobsService jobsService;

    @Inject
    public ResearchJobComponent(){

    }

    public void handleResearchClicked() {
        this.inGameController.showResearchComponent();
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void handleJob(TechnologyExtended technology) {
        
    }
}
