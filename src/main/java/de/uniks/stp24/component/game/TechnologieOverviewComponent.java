package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "TechnologieOverview.fxml")
public class TechnologieOverviewComponent extends AnchorPane {

    @FXML
    VBox technologieOverviewBox;
    @FXML
    Button closeButton;
    @FXML
    Button crewRelationsButton;
    @FXML
    Button shipbuildingButton;
    @FXML
    Button marineSienceButton;

    private String categoryName;

    private Pane parent;
    @Inject
    Subscriber subscriber;

    @Inject
    @SubComponent
    TechnologieCategoryComponent technologieCategoryComponent;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;

    @Inject
    public TechnologieOverviewComponent() {


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
    }

    public void close() {
        parent.setVisible(false);
    }

    public void shipbuilding() {
        show(technologieCategoryComponent.setCategory("shipbuilding"));
    }

    public void crewRelations() {
        show(technologieCategoryComponent.setCategory("crew_relations"));
    }

    public void marineSience() {
        show(technologieCategoryComponent.setCategory("marine_science"));
    }

    public void setContainer(@NotNull Pane parent) {
        this.parent = parent;
        parent.getChildren().add(technologieCategoryComponent);
        technologieCategoryComponent.setContainer(parent);
        technologieCategoryComponent.setVisible(false);
    }

    public void show(@NotNull TechnologieCategoryComponent technologieCategory) {
        technologieCategory.technologieCategoryBox.setVisible(true);
        setCategoryInfos(technologieCategory);
        parent.getChildren().getFirst().setVisible(true);
        parent.getChildren().getLast().setVisible(false);
    }

    public void setCategoryInfos(TechnologieCategoryComponent technologieCategory) {
        technologieCategory.technologyImage.setImage(technologieCategory.imageCache.get("assets/technologies/" + technologieCategory.technologieCategoryName + ".png"));
        String temp = technologieCategory.technologieCategoryName.replace("_", ".");
        technologieCategory.technologyNameText.setText(resources.getString("technologies." + temp));
    }
}
