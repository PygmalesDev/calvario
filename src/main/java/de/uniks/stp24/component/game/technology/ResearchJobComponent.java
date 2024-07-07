package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Technology;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.TechnologyService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
    JobsApiService jobsApiService;

    @Inject
    EventListener eventListener;

    @Inject
    TokenStorage tokenStorage;


    @Inject
    TimerService timerService;
    @Inject
    Subscriber subscriber;
    private Jobs.Job job;
    private TechnologyCategoryComponent technologyCategoryComponent;

    @Inject
    public ResearchJobComponent(){

    }

    @OnInit
    public void init(){
        PropertyChangeListener callSetProgressBar = this::setProgressBar;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_SEASON, callSetProgressBar);
    }

    public void setProgressBar(PropertyChangeEvent propertyChangeEvent) {
        this.subscriber.subscribe(this.eventListener.listen(String.format("games.%s.empires.%s.jobs.*.*",
                this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()), Jobs.Job.class), result -> {
            this.job = result.data();
            researchProgressText.setText(job.progress() + " / " + job.total());
            jobsService.onJobCompletion(job._id(), this::handleJobFinished);
            this.researchProgressBar.setProgress((((double) 1/job.total()) * job.progress()));
        }, error -> System.out.print("Error in ResearchJobComponent"));
    }

    private void handleJobFinished() {
        technologyCategoryComponent.handleJobCompleted();
        setVisible(false);
    }


    public void handleJob(TechnologyExtended technology) {
        subscriber.subscribe(jobsService.beginJob(Jobs.createTechnologyJob(technology.id())), job -> {
            this.job = job;
        });
    }

    public void setTechnologyCategoryComponent(TechnologyCategoryComponent technologyCategoryComponent) {
        this.technologyCategoryComponent = technologyCategoryComponent;
    }

    public void removeJob(){
        jobsService.stopJob(this.job._id());
        technologyCategoryComponent.handleJobCompleted();
    }
}
