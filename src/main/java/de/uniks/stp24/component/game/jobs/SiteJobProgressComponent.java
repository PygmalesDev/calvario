package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.model.Jobs.*;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "SiteJobProgress.fxml")
public class SiteJobProgressComponent extends Pane {
    @FXML
    Text jobProgressText;
    @FXML
    ProgressBar jobProgressBar;
    private double incrementAmount;
    private int progress;
    private int total;

    @Inject
    public SiteJobProgressComponent() {

    }

    public void setJobProgress(Job job) {
        this.progress = job.progress();
        this.total = job.total();
        this.jobProgressText.setText(String.format("%d/%s", this.progress, this.total));
        this.incrementAmount = (double) 1 /this.total;
        this.jobProgressBar.setProgress(this.progress*this.incrementAmount);
    }

    public void incrementProgress() {
        this.progress++;
        this.jobProgressText.setText(String.format("%d/%s", this.progress, this.total));
        this.jobProgressBar.setProgress(this.progress*this.incrementAmount);
    }

    public void cancelJob() {

    }
}
