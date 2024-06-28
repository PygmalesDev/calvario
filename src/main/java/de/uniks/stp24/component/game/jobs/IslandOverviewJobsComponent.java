package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ResourceBundle;

@Component(view = "IslandOverviewJobs.fxml")
public class IslandOverviewJobsComponent extends AnchorPane {
    @FXML
    public Text noJobText;
    @FXML
    ListView<Job> jobProgressListView;
    @Inject
    IslandAttributeStorage islandAttributes;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    App app;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    Provider<IslandOverviewJobProgressComponent> progressPaneProvider;

    private ObservableList<Job> jobsList;
    private ObservableList<Job> relatedJobs;
    private ListChangeListener<Job> relatedJobsListener = c -> {};


    @Inject
    IslandOverviewJobsComponent() {
    }

    @OnRender
    public void setJobProgressPane() {
        this.jobProgressListView.setItems(relatedJobs);
        this.jobProgressListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.progressPaneProvider));
    }


    public void insertIslandName() {
        this.noJobText.setText(this.noJobText.getText()
                .replace("{ISLAND_NAME}", this.islandAttributes.getIslandNameTranslated()));
    }

    public void setJobsObservableList(ObservableList<Job> observer) {
        this.jobsList = observer;

    }

    public void setRelatedJobsObserver() {
        this.jobsList.removeListener(this.relatedJobsListener);

        this.relatedJobs.clear();
        this.relatedJobsListener = c -> this.relatedJobs.addAll(c.getList().stream()
                    .filter(job -> job.getSystem().equals(this.tokenStorage.getIsland().id()))
                    .toList());

        this.jobsList.addListener(this.relatedJobsListener);
    }
}
