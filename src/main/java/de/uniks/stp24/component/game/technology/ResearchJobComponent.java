package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.model.Effect;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.TechnologyService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Objects;

@Component(view = "ResearchJob.fxml")
public class ResearchJobComponent extends AnchorPane {

    @FXML
    ImageView researchCostImage;
    @FXML
    ImageView technologyTagImage2;
    @FXML
    ImageView technologyTagImage1;
    @FXML
    ListView<String> technologyEffectsListView;
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
    TechnologyService technologyService;

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

    Observable<TechnologyExtended> technology;

    ArrayList<Jobs.Job> jobList = new ArrayList<>();
    private TechnologyCategoryComponent technologyCategoryComponent;

    public ObservableList<TechnologyExtended> technologies = FXCollections.observableArrayList();
    private String tag;

    public boolean showSocietyProgress;
    public boolean showEngineeringProgress;
    public boolean showComputingProgress;


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

    }

    public void progressHandling(){
        for (Jobs.Job job1 : jobList) {
            if (job1.technology().equals(technologyCategoryComponent.getTechnology().id())){
                subscriber.subscribe(jobsApiService.getJobByID(tokenStorage.getGameId(), tokenStorage.getEmpireId(), job1._id()), currentJob -> {
                    System.out.println(currentJob.progress() + " PROGRESS");
                    researchProgressBar.setProgress((double) currentJob.progress() / currentJob.total());
                    researchProgressText.setText(currentJob.progress() + " / " + currentJob.total());
                    this.job = currentJob;
                    jobsService.onJobCompletion(currentJob._id(), this::handleJobFinished);
                });
            }
        }
    }


    public void setProgressBar() {
        this.jobsService.onJobTypeProgress("technology", this::progressHandling);
    }

    public void setEffectListView(){
        if (technologyCategoryComponent.getTechnology() != null){
            technologyEffectsListView.getItems().clear();
            for (Effect effect : technologyCategoryComponent.getTechnology().effects()) {
                String effectString = effect.multiplier() + " * " + effect.variable();
                technologyEffectsListView.getItems().add(effectString);
            }
        }
    }

    private void handleJobFinished() {
        jobList.remove(job);
        technologies.removeIf(technologyExtended -> technologyExtended.id().equals(job.technology()));

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
        subscriber.subscribe(jobsService.beginJob(Jobs.createTechnologyJob(technology.id())), job1 -> {
            jobList.add(job1);
            System.out.println("JOBBER");
            System.out.println(jobList);
            this.job = job1;

            subscriber.subscribe(technologyService.getTechnology(job.technology()), result -> {
                if (!technologies.contains(result)){
                    technologies.add(result);
                }

            });
        });
    }

    public void setTechnologyCategoryComponent(TechnologyCategoryComponent technologyCategoryComponent) {
        this.technologyCategoryComponent = technologyCategoryComponent;
    }

    public void removeJob(){
        subscriber.subscribe(jobsService.stopJob(this.job._id()));
        technologyCategoryComponent.handleJobCompleted(job);
    }


    public void setTag(String tag) {
        this.tag = tag;

    }
}
