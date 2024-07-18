package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Component(view = "TechnologyCategory.fxml")
public class TechnologyCategoryComponent extends AnchorPane {

    @FXML
    public ListView<TechnologyExtended> unlockedListView;
    @FXML
    public ListView<TechnologyExtended> researchListView;
    @FXML
    public Button closeButton;
    @FXML
    public Text technologyNameText;
    @FXML
    public ImageView technologyImage;
    @FXML
    public VBox technologieCategoryBox;
    @FXML
    public Label currentResearchResourceLabel;
    @FXML
    public VBox researchLeftVBox;
    @FXML
    public StackPane researchJobContainer;
    @FXML
    public Text researchText;
    String technologieCategoryName;
    @Inject
    App app;
    @Inject
    TechnologyService technologyService;
    @Inject
    @Resource
    @Named("technologiesResourceBundle")
    public ResourceBundle technologiesResourceBundle;

    Provider<TechnologyCategorySubComponent> provider = () -> new TechnologyCategorySubComponent(this, technologyService, app, technologiesResourceBundle);

    ObservableList<TechnologyExtended> unlockedTechnologies = FXCollections.observableArrayList();
    ObservableList<TechnologyExtended> researchTechnologies = FXCollections.observableArrayList();

    private Pane parent;



    @Inject
    ResourcesService resourcesService;

    @Inject
    Subscriber subscriber;

    @Inject
    @SubComponent
    public ResearchJobComponent researchJobComponent;

    ImageCache imageCache = new ImageCache();

    boolean societyJobRunning = false;
    boolean engineeringJobRunning = false;
    boolean physicsJobRunning = false;


    PopupBuilder popupTechResearch = new PopupBuilder();
    private TechnologyExtended technology;
    private TechnologyOverviewComponent technologyOverviewComponent;



    @Inject
    public TechnologyCategoryComponent() {
    }

    @OnInit
    public void init() {
        researchJobComponent.setTechnologyCategoryComponent(this);
    }


    @OnRender
    public void render() {
        researchJobContainer.setMouseTransparent(true);
        researchJobComponent.setMouseTransparent(true);
    }

    @OnDestroy
    public void destroy() {
        if (subscriber != null) {
            subscriber.dispose();
        }
        unlockedTechnologies.clear();
        researchTechnologies.clear();

        unlockedListView.getItems().clear();
        researchListView.getItems().clear();
    }

    public void close() {
        this.setVisible(false);
    }

    /**
     * Is called when the triangle Button is clicked and resets both ListViews
     * for the next category selection
     */
    public void goBack() {
        unlockedListView.getItems().clear();
        researchListView.getItems().clear();

        parent.getChildren().getFirst().setVisible(false);
        parent.getChildren().getLast().setVisible(true);
    }

    /**
     * Is called after the category is selected in TechnologyOverviewComponent
     * it sets the category and loads both ListViews (unlocked and research) of Technologies
     * with the tag of the category
     */
    public TechnologyCategoryComponent setCategory(String category) {
        currentResearchResourceLabel.setText(String.valueOf(resourcesService.getResourceCount("research")));

        this.technologieCategoryName = category;

        unlockedTechnologies = technologyService.getUnlockedTechnologies(technologieCategoryName);
        researchTechnologies = technologyService.getResearchTechnologies(technologieCategoryName);

        researchListView.setSelectionModel(null);
        unlockedListView.setSelectionModel(null);

        unlockedListView.setItems(unlockedTechnologies);
        researchListView.setItems(researchTechnologies);

        unlockedListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.provider));
        researchListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.provider));

        return this;
    }

    public void setContainer(Pane parent) {
        this.parent = parent;
    }

    public void showResearchComponent(TechnologyExtended technology) {

        this.technology = technology;
        Map<String, Integer> technologyCostMap = new HashMap<>();
        technologyCostMap.put("research", technology.cost() * 100);
        if (resourcesService.hasEnoughResources(technologyCostMap)) {
            switch (technologieCategoryName) {
                case "society" -> {societyJobRunning = true;
                    handleJobRunning(societyJobRunning, technology);
                                    }
                case "engineering" -> {engineeringJobRunning = true;
                    handleJobRunning(engineeringJobRunning, technology);
                                        }
                case "physics" -> {physicsJobRunning = true;
                    handleJobRunning(physicsJobRunning, technology);}
            }
        }
        researchJobComponent.setEffectListView();
    }

    public TechnologyExtended getTechnology(){
        return technology;
    }

    public void setTechnology(TechnologyExtended technology){
        this.technology = technology;
    }

    private void handleJobRunning(boolean booleanJobRunning, TechnologyExtended technology) {
        if (booleanJobRunning){
            researchJobContainer.setMouseTransparent(false);
            researchJobComponent.setMouseTransparent(false);
            researchLeftVBox.setVisible(false);
            Platform.runLater(() -> {
                technologieCategoryBox.getStyleClass().clear();
                technologieCategoryBox.getStyleClass().add("technologiesActualResearchBackground");
            });
            popupTechResearch.showPopup(researchJobContainer, researchJobComponent);
            researchJobComponent.handleJob(technology);
        } else {
            unShowJobWindow();
        }
    }

    public void unShowJobWindow(){
        researchJobContainer.setMouseTransparent(true);
        researchJobComponent.setMouseTransparent(true);
        researchLeftVBox.setVisible(true);
        researchJobContainer.setVisible(false);
        researchJobComponent.setVisible(false);
        Platform.runLater(() -> {
            technologieCategoryBox.getStyleClass().clear();
            technologieCategoryBox.getStyleClass().add("technologiesCategoryBackground");
        });
    }

    public void showJobWindow() {
        researchJobComponent.setMouseTransparent(false);
        researchLeftVBox.setVisible(false);
        Platform.runLater(() -> {
            technologieCategoryBox.getStyleClass().clear();
            technologieCategoryBox.getStyleClass().add("technologiesActualResearchBackground");
        });
        popupTechResearch.showPopup(researchJobContainer, researchJobComponent);

    }

    public void handleJobCompleted(Jobs.Job job){
        switch (job._id()) {
            case "society" -> societyJobRunning = false;
            case "engineering" -> engineeringJobRunning = false;
            case "physics" -> physicsJobRunning = false;
        }



        researchJobContainer.setMouseTransparent(true);
        researchJobComponent.setMouseTransparent(true);
        researchLeftVBox.setVisible(true);
        researchJobContainer.setVisible(false);
        researchJobComponent.setVisible(false);
        Platform.runLater(() -> {
            technologieCategoryBox.getStyleClass().clear();
            technologieCategoryBox.getStyleClass().add("technologiesCategoryBackground");
        });
    }


    public void setTechnologyCategoryOverviewComponent(TechnologyOverviewComponent technologyOverviewComponent) {
        this.technologyOverviewComponent = technologyOverviewComponent;
    }

    public void showWindowOnStart() {
        technologyOverviewComponent.showWindow();
    }
}
