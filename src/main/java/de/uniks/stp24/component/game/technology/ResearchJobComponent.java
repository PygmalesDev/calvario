package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Technology;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.TechnologyService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;

@Component(view = "ResearchJob.fxml")
public class ResearchJobComponent extends AnchorPane {

    @FXML
    ImageView researchCostImage;
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

    private String jobIdSociety;

    private String jobIdEngineering;

    private String jobIdComputing;




    @Inject
    public ResearchJobComponent(){

    }

    @OnRender
    public void render(){
        cancelResearchButton.getStyleClass().clear();
        Image image = new Image("/de/uniks/stp24/assets/buttons/cancel_button.png");
        Image image1 = new Image("de/uniks/stp24/icons/resources/research.png");
        researchCostImage.setImage(image1);
        researchCostImage.setFitHeight(20);
        researchCostImage.setFitWidth(20);
        ImageView imageView = new ImageView(image);
        cancelResearchButton.setGraphic(imageView);
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
            if (job.technology().equals(technologyCategoryComponent.getTechnology().id())){
                researchProgressText.setText(job.progress() + " / " + job.total());
                this.researchProgressBar.setProgress((((double) 1/job.total()) * job.progress()));
            }
            jobsService.onJobCompletion(job._id(), this::handleJobFinished);
        }, error -> System.out.print("Error in ResearchJobComponent"));

    }

    private void handleJobFinished() {
        technologyCategoryComponent.handleJobCompleted(job);
        setVisible(false);
    }


    public void handleJob(TechnologyExtended technology) {
        // Check if there are at least two tags
        if (technology.tags().length > 0) {
            Image image1 = new Image(Constants.technologyIconMap.get(technology.tags()[0]));
            technologyTagImage1.setImage(image1);
        }
        if (technology.tags().length > 1) {
            Image image2 = new Image(Constants.technologyIconMap.get(technology.tags()[1]));
            technologyTagImage2.setImage(image2);
        }

        int cost = technology.cost() * 100;
        researchCostText.setText(String.valueOf(cost));
        technologyNameText.setText(technology.id());
        subscriber.subscribe(jobsService.beginJob(Jobs.createTechnologyJob(technology.id())), job -> {
            this.job = job;
        });
    }

    public void setTechnologyCategoryComponent(TechnologyCategoryComponent technologyCategoryComponent) {
        this.technologyCategoryComponent = technologyCategoryComponent;
    }

    public void removeJob(){
        subscriber.subscribe(jobsService.stopJob(this.job._id()));
        technologyCategoryComponent.handleJobCompleted(job);
    }
}
