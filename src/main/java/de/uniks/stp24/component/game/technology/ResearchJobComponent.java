package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Technology;
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
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

@Component(view = "ResearchJob.fxml")
public class ResearchJobComponent extends AnchorPane {

    @FXML
    ImageView technologyTagImage2;
    @FXML
    ImageView technologyTagImage1;
    @FXML
    ListView<Technology> technologyEffectsListView;
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
    @Inject
    JobsService jobsService;
    @Inject
    Subscriber subscriber;

    @Inject
    public ResearchJobComponent(){

    }

    public void handleResearchClicked() {

    }


    public void handleJob(TechnologyExtended technology) {
        subscriber.subscribe(jobsService.beginJob(Jobs.createTechnologyJob(technology.id())), result -> {
            jobsService.onJobCompletion(result._id(), () -> {
                
            });
        });
    }
}
