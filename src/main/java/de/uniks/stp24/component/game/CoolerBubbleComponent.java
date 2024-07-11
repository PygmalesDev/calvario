package de.uniks.stp24.component.game;

import de.uniks.stp24.component.Captain;
import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.model.Announcement;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.AnnouncementsService;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Component(view = "HintBubble.fxml")
public class CoolerBubbleComponent extends Captain {

    @FXML
    Button forwardButton;
    @FXML
    Button nextButton;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    EventListener eventListener;
    @Inject
    ResourcesService resourcesService;
    @Inject
    JobsService jobsService;
    @Inject
    JobsApiService jobsApiService;
    @Inject
    AnnouncementsService announcementsService;
    @Inject
    EmpireService empireService;
    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    Random random = new Random();

    int lastPeriod = 0;

    int hintCountdown;
    ArrayList<String> possibleHints = Constants.hints;

    ObservableList<Announcement> announcements;

    @Inject
    public CoolerBubbleComponent(){

    }

    @OnInit
    public void init() {
        setHintCountDown();
        announcements = announcementsService.getAnnouncements();
        // todo delete
        // harcoded for team meeting demo
//        announcementsService.addAnnouncement(new Resource("resource.doubloons", 5, 0))
//                .addAnnouncement(new Resource("resource.provisions", 5, 0))
//                .addAnnouncement(new Resource("resource.coal", 5, 0));

        this.jobsService.onJobCommongCompletion(announcementsService::addAnnouncement);
//
//        this.subscriber.subscribe(this.jobsApiService.getEmpireJobs(
//                        this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()), jobList -> {
//                    jobList.forEach(job -> jobsService.onJobCompletion(job._id(), () -> {
//                            System.out.println("JOBS DONE");
//                            announcementsService.addAnnouncement(job);
//                    }));
//                }, error -> System.out.println("Failed to load job collections \n" + error.getMessage())
//        );
    }

    private void setHintCountDown() {
        // todo change
        // countdown = random.nextInt(20, 40);
        hintCountdown = 1;
    }

    @OnRender
    public void render() {
        rotateCaptain();

        announcements.addListener((ListChangeListener<Object>) change -> {
            if (announcements.isEmpty()) {
                nextButton.getStyleClass().removeAll("rightTriangleButton");
                nextButton.getStyleClass().add("closeButtonHint");
            }
            else  {
                nextButton.getStyleClass().removeAll("closeButtonHint");
                nextButton.getStyleClass().add("rightTriangleButton");
            }
        });

        subscriber.subscribe(this.eventListener.listen(String.format("games.%s.ticked", tokenStorage.getGameId()), Game.class),
                result -> {
                    if (lastPeriod != result.data().period()) {
                        lastPeriod = result.data().period();

                        subscriber.subscribe(empireService.getResourceAggregates(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                                aggregateResultDto -> {
                                    // announcementsService.clearAnnouncements();
                                    for (AggregateItemDto item : aggregateResultDto.items()) {
                                        Resource resource = resourcesService.aggregateItemDtoToResource(item);
                                        if (resource.count() > 0 && resource.count() + item.subtotal() <= 0) {
                                            announcementsService.addAnnouncement(resource);
                                        }
                                    }
                                },
                                error -> System.out.println("ErrorAggregateSubscriber: " + error));

                        decideWhatToSay();
                    }
                },
                error -> System.out.println("Error on Season: " + error.getMessage())
        );
    }

    public void forward() {

    }

    public void talk(String text) {
        setVisible(true);
        setCaptainText(text);
    }

    private void announce() {
        Announcement announcement = announcementsService.getNextAnnouncement();
        forwardButton.setVisible(announcement.showForward());
        talk(announcement.message());
    }

    public void decideWhatToSay(){
        if (announcements.isEmpty()) { // todo check for jobs
//            sayTip();
        } else {
            announce();
        }
    }

    private void sayTip() {
        hintCountdown -= 1;
        if (hintCountdown <= 0) {
            talk(gameResourceBundle.getString(possibleHints.get(random.nextInt(possibleHints.size()))));
            setHintCountDown();
        } else {
            silence();
        }
    }

    @OnKey(code = KeyCode.S, alt = true)
    public void removeAltS(){
        silence();
        possibleHints.remove("hint.alt.s");
    }

    @OnKey(code = KeyCode.H, alt = true)
    public void removeAltH(){
        silence();
        possibleHints.remove("hint.alt.h");
    }

    @OnKey(code = KeyCode.E, alt = true)
    public void removeAltE(){
        silence();
        possibleHints.remove("hint.alt.e");
    }

    public void silence() {
        announcements.clear();
        setVisible(false);
    }
}
