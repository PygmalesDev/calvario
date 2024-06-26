package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.model.Jobs.Job;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "JobElement.fxml")
public class JobElementComponent extends Pane implements ReusableItemComponent<Job> {
    @FXML
    public ImageView jobImage;
    @FXML
    public Text jobNameText;
    @FXML
    public Text jobTypeText;
    @FXML
    public Text timerText;
    @FXML
    Button jobCancelButton;

    @Inject
    public JobElementComponent() {

    }

    @Override
    public void setItem(@NotNull Job job) {
    }
}
