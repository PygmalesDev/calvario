package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class TechnologyService {

    @Inject
    public PresetsApiService presetsApiService;
    @Inject
    public EmpireApiService empireApiService;
    @Inject
    public GameLogicApiService gameLogicApiService;
    @Inject
    public Subscriber subscriber;

    @Inject
    public TokenStorage tokenStorage;

    ObservableList<TechnologyExtended> unlockedList = FXCollections.observableArrayList();
    ObservableList<TechnologyExtended> researchList = FXCollections.observableArrayList();
    ObservableList<ObservableList<TechnologyExtended>> unlockedAndResearchedList = FXCollections.observableArrayList();
    List<TechnologyExtended> technologiesList = new ArrayList<>();
    @Inject
    public EventListener eventListener;

    String[] lastUpdate = new String[]{""};

    @Inject
    public TechnologyService() {
    }

    public void createEmpireListener(Runnable runnable) {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".empires." + tokenStorage.getEmpireId() + ".updated", EmpireDto.class),
                event -> {
                    if (!Arrays.equals(lastUpdate, event.data().technologies())) {
                        System.out.println("Technologies: " + Arrays.toString(event.data().technologies()));
                        runnable.run();
                        lastUpdate = event.data().technologies();
                    }
                },
                error -> System.out.println("errorListener: " + error)
        );
        this.subscriber.subscribe(() -> System.out.println("event listener stopped!"));
    }

    public ObservableList<TechnologyExtended> getAllUnlocked() {
        ObservableList<TechnologyExtended> unlocked = FXCollections.observableArrayList();
        subscriber.subscribe(empireApiService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                empire -> {
                    unlocked.clear();
                    if (empire.technologies() != null) {
                        for (String techId : empire.technologies()) {
                            subscriber.subscribe(getTechnology(techId),
                                    unlocked::add, error -> System.out.println("Error after try to get Technology " + techId + " because: " + error.getMessage()));
                        }
                    }
                }, error -> System.out.println("Error after try to get empire because of: " + error.getMessage()));
        return unlocked;
    }

    public ObservableList<ObservableList<TechnologyExtended>> getAllUnlockedAndResearched() {
        ObservableList<TechnologyExtended> unlocked = FXCollections.observableArrayList();
        ObservableList<TechnologyExtended> research = FXCollections.observableArrayList();
        unlockedAndResearchedList.add(unlocked);
        unlockedAndResearchedList.add(research);
        unlockedList.clear();
        researchList.clear();
        subscriber.subscribe(empireApiService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                empire -> {
                    if (empire.technologies() != null) {
                        for (String techId : empire.technologies()) {
                            subscriber.subscribe(getTechnology(techId),
                                    unlocked::add, error -> System.out.println("Error after try to get Technology " + techId + " because: " + error.getMessage()));
                        }
                    }
                    unlockedList = unlocked;
                    unlockedAndResearchedList.add(unlocked);
                                for (TechnologyExtended tech : technologiesList) {
                                    if (unlocked.stream().noneMatch(technology -> technology.id().equals(tech.id())) && research.stream().noneMatch(techEx -> techEx.id().equals(tech.id()))) {
                                        research.add(tech);
                                    }
                                }
                                researchList = research;
                                unlockedAndResearchedList.add(research);
                            }, error -> System.out.println("Error after try to get all technologies"));
        return unlockedAndResearchedList;
    }

    public ObservableList<ObservableList<TechnologyExtended>> getUnlockedAndResearch(String tag) {
        ObservableList<TechnologyExtended> unlocked;
        unlocked = FXCollections.observableArrayList();
        ObservableList<TechnologyExtended> research;
        research = FXCollections.observableArrayList();
        ObservableList<ObservableList<TechnologyExtended>> unlockedAndResearch = FXCollections.observableArrayList();
        unlockedAndResearch.add(unlocked);
        unlockedAndResearch.add(research);
        unlockedList.clear();
        researchList.clear();
        subscriber.subscribe(empireApiService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                empire -> {
                    if (empire.technologies() != null) {
                        for (String techId : empire.technologies()) {
                            subscriber.subscribe(getTechnology(techId),
                                    technology -> {
                                        if (Arrays.asList(technology.tags()).contains(tag)) {
                                            unlocked.add(technology);
                                        }
                                    }, error -> System.out.println("Error after try to get Technology " + techId + " because: " + error.getMessage()));
                        }
                        unlockedList = unlocked;
                        unlockedAndResearch.add(unlocked);
                        subscriber.subscribe(getTechnologies(),
                                techList -> {
                                    for (TechnologyExtended tech : techList) {
                                        if (unlocked.stream().noneMatch(technology -> technology.id().equals(tech.id())) && Arrays.asList(tech.tags()).contains(tag)) {
                                            if (tech.requires() == null || tech.requires().length == 0) {
                                                research.add(tech);
                                            } else if (Arrays.stream(tech.requires()).allMatch(techId -> unlocked.stream().anyMatch(technology -> technology.id().equals(techId)))) {
                                                research.add(tech);
                                            }
                                        }
                                    }
                                    researchList = research;
                                    unlockedAndResearch.add(research);
                                }, error -> System.out.println("Error after try to get all technologies"));
                    }
                }, error -> System.out.println("Error after try to get empire because of: " + error.getMessage()));
        return unlockedAndResearch;
    }

    public ObservableList<ObservableList<TechnologyExtended>> getUnlockedAndResearchList() {
        return unlockedAndResearchedList;
    }

    public ObservableList<TechnologyExtended> getUnlockedList() {
        return unlockedList;
    }

    public ObservableList<TechnologyExtended> getResearchList() {
        return researchList;
    }

    public void initAllTechnologies() {
        subscriber.subscribe(getTechnologies(),
                techList -> {
                    technologiesList.clear();
                    technologiesList.addAll(techList);
                }, error -> System.out.println("Error after try to get all technologies"));
    }

    public List<TechnologyExtended> getTechnologiesList() {
        return technologiesList;
    }

    public Observable<TechnologyExtended> getTechnology(String id) {
        return presetsApiService.getTechnology(id);
    }

    public Observable<ArrayList<TechnologyExtended>> getTechnologies() {
        return presetsApiService.getTechnologies();
    }

    public Observable<AggregateResultDto> getTechnologyTimeAndCost(String empireID, String aggregate, String techID) {
        return gameLogicApiService.getTechnologyCostAndTime(empireID, aggregate, techID);
    }
}