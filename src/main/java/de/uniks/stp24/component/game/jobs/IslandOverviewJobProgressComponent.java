package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.model.Jobs.Job;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "IslandOverviewJobProgress.fxml")
public class IslandOverviewJobProgressComponent extends Pane implements ReusableItemComponent<Job> {
    @FXML
    ProgressBar jobProgressBar;
    @FXML
    Text jobTimeRemaining;
    @FXML
    Button stopJobButton;
    @FXML
    Button infoJobButton;
    @FXML
    Text jobDescriptionText;
    @FXML
    ImageView jobImage;
    @FXML
    HBox costsHBox;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    public IslandOverviewJobProgressComponent() {

    }
    @Override
    public void setItem(@NotNull Job job) {
        this.jobDescriptionText.setText(String.format("%s of %s", job.type(), job.building()));
        this.jobTimeRemaining.setText(String.format("%s/%s", job.progress(), job.total()));
    }
}
