package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Technology;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.collections.FXCollections;
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
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ResourceBundle;

@Component(view = "TechnologyCategory.fxml")
public class TechnologyCategoryComponent extends AnchorPane {

    @FXML
    public ListView<Technology> unlockedListView;
    @FXML
    public ListView<Technology> researchListView;
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
    TechnologyService technologyService;

    Provider<TechnologyCategorySubComponent> researchComponentProvider = TechnologyCategorySubComponent::new;
    Provider<TechnologyCategorySubComponent> unlockedComponentProvider = TechnologyCategorySubComponent::new;

    ObservableList<TechnologyExtended> unlockedTechnologies = FXCollections.observableArrayList();
    ObservableList<TechnologyExtended> researchTechnologies = FXCollections.observableArrayList();

    private Pane parent;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;

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
