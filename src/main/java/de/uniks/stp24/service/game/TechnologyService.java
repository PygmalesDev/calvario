package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.TokenStorage;
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

    String category;

    ObservableList<TechnologyExtended> unlockedList = FXCollections.observableArrayList();
    ObservableList<TechnologyExtended> researchList = FXCollections.observableArrayList();


    @Inject
    public TechnologyService() {
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

    public ObservableList<TechnologyExtended> getUnlocked(String tag) {
        ObservableList<TechnologyExtended> unlocked = getAllUnlocked();
        ObservableList<TechnologyExtended> unlockedTag = FXCollections.observableArrayList();
        for (TechnologyExtended tech : unlocked) {
            if ((tech.precedes() == null || tech.precedes().length == 0) && unlockedTag.stream().noneMatch(technology -> technology.id().equals(tech.id())) && Arrays.asList(tech.tags()).contains(tag)) {
                unlockedTag.add(tech);
            } else if (tech.precedes() != null){
                for (String pre : tech.precedes()) {
                    if (unlocked.stream().noneMatch(technology -> technology.id().equals(pre)) && Arrays.asList(tech.tags()).contains(tag)) {
                        unlockedTag.add(tech);
                    }
                }
            }
        }
        return unlockedTag;
    }

    public ObservableList<TechnologyExtended> getResearch(String tag) {
        ObservableList<TechnologyExtended> unlocked = getAllUnlocked();
        ObservableList<TechnologyExtended> research = FXCollections.observableArrayList();
        for (TechnologyExtended tech : unlocked) {
            for (String techId : tech.requires()) {
                subscriber.subscribe(getTechnology(techId), technology -> {
                    if (Arrays.asList(technology.tags()).contains(tag) && research.stream().noneMatch(technologyExtended -> technologyExtended.id().equals(technology.id()))) {
                        research.add(technology);
                    }
                }, error -> System.out.println("Error after try to get Technology " + techId + " because: " + error.getMessage()));
            }
        }
        // For Technologies that doesn't have any requirements
        subscriber.subscribe(getTechnologies(), techList -> {
            for (TechnologyExtended tech : techList) {
                if (tech.requires() == null && unlocked.stream().noneMatch(technology -> technology.id().equals(tech.id())) && research.stream().noneMatch(technology -> technology.id().equals(tech.id()))) {
                    research.add(tech);
                }
            }
        }, error -> System.out.println("Error try to get technology list"));

        return research;
    }

    public ObservableList<ObservableList<TechnologyExtended>> getAllUnlockedAndResearched() {
        ObservableList<TechnologyExtended> unlocked = FXCollections.observableArrayList();
        ObservableList<TechnologyExtended> research = FXCollections.observableArrayList();
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
                                    unlocked::add, error -> System.out.println("Error after try to get Technology " + techId + " because: " + error.getMessage()));
                        }
                    }
                    unlockedList = unlocked;
                    unlockedAndResearch.add(unlocked);
                    subscriber.subscribe(getTechnologies(),
                            techList -> {
                                for (TechnologyExtended tech : techList) {
                                    if (unlocked.stream().noneMatch(technology -> technology.id().equals(tech.id())) && research.stream().noneMatch(techEx -> techEx.id().equals(tech.id()))) {
                                        research.add(tech);
                                    }
                                }
                                researchList = research;
                                unlockedAndResearch.add(research);
                            });
                }, error -> System.out.println("Error after try to get empire because of: " + error.getMessage()));
        return unlockedAndResearch;
    }

    public ObservableList<TechnologyExtended> getAllResearch() {
        ObservableList<TechnologyExtended> research = FXCollections.observableArrayList();
        ObservableList<TechnologyExtended> unlocked = getAllUnlocked();
        subscriber.subscribe(getTechnologies(),
                technologiesList -> {
                    research.clear();
                    for (TechnologyExtended tech : technologiesList) {
                        if (unlocked.stream().noneMatch(techEx -> techEx.id().equals(tech.id())) && research.stream().noneMatch(techEx -> techEx.id().equals(tech.id()))) {
                            research.add(tech);
                        }
                    }
                    System.out.println("Research" + research);
                }, error -> System.out.println("Error after try to get all technologies"));
        return research;
    }

    public ObservableList<TechnologyExtended> getUnlockedList() {
        return unlockedList;
    }

    public ObservableList<TechnologyExtended> getResearchList() {
        return researchList;
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

    public void setCategory(String category) {
        this.category = category;
    }
}