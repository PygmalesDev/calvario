package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.App;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

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

        unlockedTechnologies = technologyService.getUnlockedTechnologies();
        researchTechnologies = technologyService.getResearchTechnologies();

            unlockedListView.setItems(unlockedTechnologies);
            researchListView.setItems(researchTechnologies);

            System.out.println("In TechnologyCategoryComponent render() method (Unlocked)" + unlockedListView.getItems());
            System.out.println("In TechnologyCategoryComponent render() method (Research)" + researchListView.getItems());

            unlockedListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.provider));
            researchListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.provider));

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

    public void goBack() {
        parent.getChildren().getFirst().setVisible(false);
        parent.getChildren().getLast().setVisible(true);
    }

    public TechnologyCategoryComponent setCategory(String category) {
        this.technologieCategoryName = category;
        return this;
    }

    public void setContainer(Pane parent) {
        this.parent = parent;
    }
}
