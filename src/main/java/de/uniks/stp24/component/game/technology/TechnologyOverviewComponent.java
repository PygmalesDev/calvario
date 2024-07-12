package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "TechnologyOverview.fxml")
public class TechnologyOverviewComponent extends AnchorPane {

    @FXML
    public StackPane researchJobContainer;
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
    TechnologyService technologyService;

    @Inject
    @SubComponent
    public TechnologyCategoryComponent technologyCategoryComponent;


    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;


    @Inject
    public TechnologyOverviewComponent() {

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

        if (technologyService.subscriber != null) {
            technologyService.subscriber.dispose();
        }
    }

    public void close() {
        this.setVisible(false);
    }

    public void engineering() {
        technologyCategoryComponent.researchJobComponent.progressHandling();
        technologyCategoryComponent.researchJobComponent.setEffectListView();
        show(technologyCategoryComponent.setCategory("engineering"));
        outerLoop:
        for (TechnologyExtended technology : technologyCategoryComponent.researchJobComponent.technologies) {
            for (String tag : technology.tags()) {
                if (Objects.equals(tag, "engineering")){
                    technologyCategoryComponent.researchJobComponent.technologyNameText.setText(technology.id());
                    technologyCategoryComponent.setTechnology(technology);
                    technologyCategoryComponent.showJobWindow();
                    break outerLoop;
                } else {
                    technologyCategoryComponent.unShowJobWindow();
                }
            }
        }

    }

    public void society() {
        technologyCategoryComponent.researchJobComponent.progressHandling();
        technologyCategoryComponent.researchJobComponent.setEffectListView();
        show(technologyCategoryComponent.setCategory("society"));
        technologyCategoryComponent.researchJobComponent.setTag("society");
        outerLoop:
        for (TechnologyExtended technology : technologyCategoryComponent.researchJobComponent.technologies) {
            for (String tag : technology.tags()) {
                if (Objects.equals(tag, "society")){
                    technologyCategoryComponent.researchJobComponent.technologyNameText.setText(technology.id());
                    technologyCategoryComponent.setTechnology(technology);
                    technologyCategoryComponent.showJobWindow();
                    break outerLoop;
                } else {
                    technologyCategoryComponent.unShowJobWindow();
                }
            }
        }

    }

    public void physics() {
        technologyCategoryComponent.researchJobComponent.progressHandling();
        technologyCategoryComponent.researchJobComponent.setEffectListView();
        show(technologyCategoryComponent.setCategory("physics"));
        outerLoop:
        for (TechnologyExtended technology : technologyCategoryComponent.researchJobComponent.technologies) {
            for (String tag : technology.tags()) {
                if (Objects.equals(tag, "computing")){
                    technologyCategoryComponent.researchJobComponent.technologyNameText.setText(technology.id());
                    technologyCategoryComponent.setTechnology(technology);
                    technologyCategoryComponent.showJobWindow();
                    break outerLoop;
                } else {
                    technologyCategoryComponent.unShowJobWindow();
                }
            }
        }

    }


    public void setContainer(@NotNull Pane parent) {
        this.parent = parent;
        parent.getChildren().add(technologyCategoryComponent);
        technologyCategoryComponent.setContainer(parent);
        technologyCategoryComponent.setVisible(false);
    }

    /**
     * First Child: TechnologyCategoryComponent
     * Second Child: TechnologyOverviewComponent
     */
    public void show(@NotNull TechnologyCategoryComponent technologieCategory) {
        setCategoryInfos(technologieCategory);
        parent.getChildren().getFirst().setVisible(true);
        parent.getChildren().getLast().setVisible(false);

    }

    public void setCategoryInfos(@NotNull TechnologyCategoryComponent technologieCategory) {
        technologieCategory.technologyImage.setImage(technologieCategory.imageCache.get("assets/technologies/categories/" +technologieCategory.technologieCategoryName + ".png"));
        String technologyKey = technologieCategory.technologieCategoryName.replace("_", ".");
        technologieCategory.technologyNameText.setText(resources.getString("technologies." + technologyKey));
    }
}
