package de.uniks.stp24.game.jobs;

import de.uniks.stp24.component.game.ClaimingSiteComponent;
import de.uniks.stp24.component.game.IslandClaimingComponent;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.model.UpgradeStatus;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import javax.inject.Provider;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestIslandClaiming extends JobsTestComponent {
    @InjectMocks
    IslandClaimingComponent islandClaimingComponent;

    Jobs.Job explorationJob = new Jobs.Job("0", "0", "jobID_12", 0, 6,
            this.GAME_ID, this.EMPIRE_ID, "ISLAND_UNEXP", 0, "upgrade", null,
            null, null, Map.of("energy", 200), null);

    Provider<ClaimingSiteComponent> componentProvider = () -> {
        var component = new ClaimingSiteComponent();
        component.imageCache = this.imageCache;
        return component;
    };


    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.islandAttributeStorage.systemUpgradeAttributes = new SystemUpgrades(
                null, null, new UpgradeStatus("0","upgraded", 1, 0, Map.of("energy", 100),
                Map.of("energy", 100), 0), null, null);

        doReturn(null).when(this.imageCache).get(any());

        this.islandClaimingComponent.componentProvider = this.componentProvider;
        this.islandClaimingComponent.islandsService = this.islandsService;
        this.islandClaimingComponent.islandAttributes = this.islandAttributeStorage;
        this.islandClaimingComponent.imageCache = this.imageCache;
        this.islandClaimingComponent.jobsService = this.jobsService;
        this.islandClaimingComponent.subscriber = this.subscriber;
        this.islandClaimingComponent.gameResourceBundle = this.gameResourceBundle;

        this.app.show(this.islandClaimingComponent);
        this.islandClaimingComponent.getStylesheets().clear();
    }

    @Test
    public void testOpeningUnexploredIsland() {
        Platform.runLater(() -> this.islandClaimingComponent.setIslandInformation(ISLAND_UNEXP));
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(lookup("#colonizePane").query().isVisible());
        assertTrue(lookup("#exploreButton").query().isVisible());
        assertEquals(lookup("#islandTypeText").queryText().getText(), "Lushy Island");
        assertEquals(lookup("#colonizersText").queryText().getText(), "3");
        assertEquals(lookup("#capacityText").queryText().getText(), "12");
    }

    @Test
    public void testOpeningExploredIsland() {
        Platform.runLater(() -> this.islandClaimingComponent.setIslandInformation(ISLAND_EXP));
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(lookup("#colonizePane").query().isVisible());
        assertFalse(lookup("#noSitesText").query().isVisible());
        assertEquals(lookup("#sitesListView").queryListView().getItems().size(), 1);
        assertEquals(lookup("#costsListView").queryListView().getItems().size(), 1);
        assertEquals(lookup("#consumeListView").queryListView().getItems().size(), 1);
    }

    @Test
    public void testBeginIslandExploration() {
        Platform.runLater(() -> this.islandClaimingComponent.setIslandInformation(ISLAND_UNEXP));
        WaitForAsyncUtils.waitForFxEvents();

        when(this.jobsApiService.createNewJob(any(), any(), any())).thenReturn(Observable.just(explorationJob));
        when(this.jobsApiService.deleteJob(any(), any(), any())).thenReturn(Observable.just(explorationJob));

        assertTrue(lookup("#exploreButton").query().isVisible());
        clickOn("#exploreButton");

        this.createInternally(explorationJob);
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(lookup("#exploreButton").query().isVisible());
        assertTrue(lookup("#jobProgressBar").query().isVisible());

        this.updateInternally("jobID_12");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(lookup("#jobProgressBar").queryAs(ProgressBar.class).getProgress() > 0);

        clickOn("#cancelJobButton");
    }

    @Test
    public void testOpeningWithExistingJob() {
        this.createInternally(explorationJob);
        Platform.runLater(() -> this.islandClaimingComponent.setIslandInformation(ISLAND_UNEXP));
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(lookup("#exploreButton").query().isVisible());
        assertTrue(lookup("#jobProgressBar").query().isVisible());
    }
}
