package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.App;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
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
    String technologieCategoryName;
    @Inject
    App app;

    @Inject
    TechnologyService technologyService;

    @Inject
    Provider<TechnologyCategorySubComponent> provider;

    ObservableList<TechnologyExtended> unlockedTechnologies = FXCollections.observableArrayList();
    ObservableList<TechnologyExtended> researchTechnologies = FXCollections.observableArrayList();

    private Pane parent;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;

    @Inject
    ResourcesService resourcesService;

    @Inject
    Subscriber subscriber;

    ImageCache imageCache = new ImageCache();

    @Inject
    public TechnologyCategoryComponent() {
    }

    @OnInit
    public void init() {

    }

    @OnRender
    public void render() {
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
        parent.setVisible(false);
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
}
