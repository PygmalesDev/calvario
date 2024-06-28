package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.model.Jobs.Job;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "IslandOverviewJobProgress.fxml")
public class IslandOverviewJobProgressComponent extends Pane implements ReusableItemComponent<Job> {

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    public IslandOverviewJobProgressComponent() {

    }
    @Override
    public void setItem(@NotNull Job item) {

    }
}
