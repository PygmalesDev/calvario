package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Effect;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;
import javafx.util.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.*;

@Component(view = "TechnologyCategorySubComponent.fxml")
public class TechnologyCategorySubComponent extends VBox implements ReusableItemComponent<TechnologyExtended> {
    @FXML
    public ImageView tagImage3;
    @FXML
    public ImageView tagImage2;
    @FXML
    public ImageView tagImage1;

    @FXML
    public ListView<Effect> descriptionListView;
    @FXML
    public ImageView timeImage;
    @FXML
    public Label timeLabel;
    @FXML
    public ImageView researchImage;
    @FXML
    public Label researchLabel;
    @FXML
    public Button researchButton;
    @FXML
    public HBox researchHBox;
    @FXML
    public Label technologyLabel;
    @FXML
    public Tooltip researchLabelTooltip;
    @FXML
    public Tooltip showEffectTooltip;
    @FXML
    public Label showEffectLabel;


    @Inject
    App app;

    TechnologyExtended technology;

    @Inject
    TechnologyService technologyService;

    @Inject
    @Named("technologiesResourceBundle")
    public ResourceBundle technologiesResourceBundle;

    @Inject
    @Named("variablesResourceBundle")
    public ResourceBundle variablesResourceBundle;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;
    @SubComponent
    @Inject
    public TechnologyResearchDetailsComponent technologyResearchDetailsComponent;

    @SubComponent
    @Inject
    public TechnologyEffectDetailsComponent technologyEffectDetailsComponent;

    ImageCache imageCache = new ImageCache();

    ObservableList<Effect> description = FXCollections.observableArrayList();

    Provider<TechnologyCategoryDescriptionSubComponent> provider = () -> new TechnologyCategoryDescriptionSubComponent(variablesResourceBundle);

    /**
     * This class is for the components of the listView in the technology category
     */
    @Inject
    public TechnologyCategorySubComponent() {

    }

    /**
     * Set the item inclusive the attributes tags, costs, and id
     *
     * @param technologyExtended is the technology for the subcomponent in the ListView
     */
    @Override
    public void setItem(@NotNull TechnologyExtended technologyExtended) {
        this.technology = technologyExtended;
        technologyLabel.setText(technologiesResourceBundle.getString(technologyExtended.id()));

        int i = technologyExtended.tags().length;

        if (i > 0 && technologyExtended.tags()[0] != null) {
            tagImage1.setImage(imageCache.get("assets/technologies/tags/" + technologyExtended.tags()[0] + ".png"));
        }
        if (i > 1 && technologyExtended.tags()[1] != null) {
            tagImage2.setImage(imageCache.get("assets/technologies/tags/" + technologyExtended.tags()[1] + ".png"));
        }
        if (i > 2 && technologyExtended.tags()[2] != null) {
            tagImage3.setImage(imageCache.get("assets/technologies/tags/" + technologyExtended.tags()[2] + ".png"));
        }

        technologyResearchDetailsComponent.setTechnologyInfos(technology);
        technologyEffectDetailsComponent.setTechnologyInfos(technology);


        showEffectTooltip.setGraphic(technologyEffectDetailsComponent);
        showEffectTooltip.setShowDelay(Duration.ZERO);
        showEffectTooltip.setShowDuration(Duration.INDEFINITE);

        researchLabel.setText(String.valueOf(technologyExtended.cost()));

        descriptionListView.getItems().clear();
        if (technologyExtended.effects().length != 0) {
            description.addAll(technologyExtended.effects());
            descriptionListView.setItems(description);
            descriptionListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.provider));
        }

        if (technology.effects().length > 1) {
            showEffectLabel.setVisible(true);
            showEffectLabel.setMouseTransparent(false);
        } else {
            showEffectLabel.setVisible(false);
            showEffectLabel.setMouseTransparent(true);
        }

        if (technologyService.getUnlockedTechnologies().stream().anyMatch(tech -> tech.id().equals(technology.id()))) {
            getAllChildren(this).removeAll(Arrays.asList(timeImage, timeLabel, researchImage, researchLabel, researchButton));
        }
    }

    @OnInit
    public void init() {

    }

    @OnRender
    public void render() {

        timeImage.setImage(imageCache.get("icons/time.png"));
        researchImage.setImage(imageCache.get("icons/resources/research.png"));

        researchLabelTooltip.setGraphic(technologyResearchDetailsComponent);
        researchLabelTooltip.setShowDelay(Duration.ZERO);
        researchLabelTooltip.setShowDuration(Duration.INDEFINITE);

        technologyEffectDetailsComponent.setTechnology(technology);

    }

    @OnDestroy
    public void destroy() {
        description.clear();
        descriptionListView.getItems().clear();
    }

    public List<Node> getAllChildren(Parent parent) {
        List<Node> nodes = new ArrayList<>();
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent) {
                nodes.addAll(getAllChildren((Parent) node));
            }
        }
        return nodes;
    }
}
