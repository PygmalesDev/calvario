package de.uniks.stp24.component.game;

import de.uniks.stp24.service.ImageCache;
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
import java.util.ResourceBundle;

@Component(view = "TechnologieCategory.fxml")
public class TechnologieCategoryComponent extends AnchorPane {

    @FXML
    public ListView unlockedListView;
    @FXML
    public ListView researchListView;
    @FXML
    public Button closeButton;
    @FXML
    public Text technologyNameText;
    @FXML
    public ImageView technologyImage;
    @FXML
    public VBox technologieCategoryBox;
    String technologieCategoryName;

    private Pane parent;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;

    ImageCache imageCache = new ImageCache();

    @Inject
    public TechnologieCategoryComponent() {
    }

    @OnInit
    public void init() {

    }

    @OnRender
    public void render() {
        System.out.println("render");
        if (technologieCategoryName != null) {
            technologyImage.setImage(imageCache.get("assets/technologies/" + technologieCategoryName + ".png"));
        }
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
    public TechnologieCategoryComponent setCategory(String category) {
        this.technologieCategoryName = category;
        return this;
    }

    public void setContainer(Pane parent) {
        this.parent = parent;
    }
}
