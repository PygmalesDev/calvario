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
    EmpireApiService empireApiService;
    @Inject
    GameLogicApiService gameLogicApiService;
    @Inject
    public Subscriber subscriber;

    @Inject
    TokenStorage tokenStorage;

    String category;

    List<TechnologyExtended> technologies;
    Observable<ArrayList<TechnologyExtended>> temp;
    Set<TechnologyExtended> allUnlockedTechnologiesSet = new HashSet<>();

    ObservableList<TechnologyExtended> unlockedTechnologiesList = FXCollections.observableArrayList();

    ObservableList<TechnologyExtended> allUnlockedTechnologiesList = FXCollections.observableArrayList();

    ObservableList<TechnologyExtended> researchTechnologiesList = FXCollections.observableArrayList();


    @Inject
    public TechnologyService() {
    }


//    public ObservableList<TechnologyExtended> getAllUnlockedTechnologies(String tag) {
//        temp = getTechnologies();
//
//        subscriber.subscribe(empireApiService.getEmpiresDtos(tokenStorage.getGameId()),
//                empireDtos -> {
//                    for (EmpireDto empireDto : empireDtos) {
//                        if (empireDto.user().equals(tokenStorage.getUserId()) && empireDto.technologies() != null) {
//                            for (String technology : empireDto.technologies()) {
//
//                                subscriber.subscribe(getTechnology(technology),
//                                        technologyExtended -> {
//                                            ArrayList<String> tempTags = new ArrayList<>(Arrays.asList(technologyExtended.tags()));
//                                            if (tempTags.contains(tag)) {
//                                                allUnlockedTechnologiesSet.add(technologyExtended);
//                                            }
//                                        }
//                                );
//
//                            }
//                        }
//                    }
//                },
//                error -> System.out.println("Error when getting all unlocked Technologies: " + error)
//        );
//        allUnlockedTechnologiesList = FXCollections.observableArrayList(allUnlockedTechnologiesSet);
//        return allUnlockedTechnologiesList;
//    }
//
//    public ObservableList<TechnologyExtended> getAllResearchTechnologies(String tag) {
//        ObservableList<TechnologyExtended> research = FXCollections.observableArrayList();
//        ObservableList<TechnologyExtended> unlocked = getAllUnlockedTechnologies(tag);
//
//        technologies = temp.blockingFirst();
//        for (TechnologyExtended technology : technologies) {
//            if (!unlocked.contains(technology) && Arrays.asList(technology.tags()).contains(tag)) {
//                research.add(technology);
//            }
//        }
//        return research;
//    }
//
//    /**
//     * iterate tru all unlocked Technologies independent of the tag and check if any of
//     * their precedes are in temp, if yes add them in unlockedTechnologiesList
//     *
//     * @return unlocked Technologies without precedes that are also unlocked
//     */
//    public ObservableList<TechnologyExtended> getUnlockedTechnologies(String tag) {
//            List<TechnologyExtended> tempUnlocked = getAllUnlockedTechnologies(tag);
//            unlockedTechnologiesList.clear();
//            for (TechnologyExtended technology : tempUnlocked) {
//                for (String t : technology.precedes()) {
//
//                    if (tempUnlocked.stream().noneMatch(tech -> tech.id().equals(t))) {
//                        unlockedTechnologiesList.add(technology);
//                    }
//                }
//            }
//        return unlockedTechnologiesList;
//    }
//
//    /**
//     * iterate tru all research Technologies and check their requirements
//     * if all requirements are not in temp, add them in researchTechnologiesList
//     *
//     * @return research Technologies without requirements that are not unlocked
//     */
//    public ObservableList<TechnologyExtended> getResearchTechnologies(String tag) {
//            List<TechnologyExtended> tempResearch = getResearchTechnologies();
//            List<TechnologyExtended> tempUnlocked = getUnlockedTechnologies();
//
//            for (TechnologyExtended technology : tempResearch) {
//                boolean add = true;
//                if (technology.requires() != null && !Arrays.asList(technology.requires()).isEmpty() && Arrays.asList(technology.tags()).contains(tag)) {
//                    for (String t : technology.requires()) {
//                        if (tempUnlocked.stream().noneMatch(tech -> tech.id().equals(t))) {
//                            add = false;
//                            break;
//                        }
//                    }
//                }
//                if (add && (Arrays.asList(technology.tags()).contains(tag)) && researchTechnologiesList.stream().noneMatch(tech -> tech.id().equals(technology.id()))) {
//                    researchTechnologiesList.add(technology);
//                }
//            }
//        return researchTechnologiesList;
//    }
//
//    /**
//     * get all unlocked Technologies independent of the tag
//     */
//    public ObservableList<TechnologyExtended> getUnlockedTechnologies() {
//        ObservableList<TechnologyExtended> unlocked = FXCollections.observableArrayList();
//        for (String tag : Constants.technologyTranslation.keySet()) {
//            unlocked.addAll(getUnlockedTechnologies(tag));
//        }
//        return unlocked;
//    }
//
//    /**
//     * get all research Technologies independent of the tag
//     */
//    public ObservableList<TechnologyExtended> getResearchTechnologies() {
//        ObservableList<TechnologyExtended> research = FXCollections.observableArrayList();
//        for (String tag : Constants.technologyTranslation.values()) {
//            research.addAll(getAllResearchTechnologies(tag));
//        }
//        return research;
//    }

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
                    System.out.println("Unlocked: " + unlocked);
                }, error -> System.out.println("Error after try to get empire because of: " + error.getMessage()));
        return unlocked;
    }

//    public ObservableList<TechnologyExtended> getAllUnlocked(String tag) {
//
//    }
//
//    public ObservableList<TechnologyExtended> getUnlocked(String tag) {
//
//    }

    public ObservableList<ObservableList<TechnologyExtended>> getAllUnlockedAndReserach() {
        ObservableList<TechnologyExtended> unlocked = FXCollections.observableArrayList();
        ObservableList<TechnologyExtended> research = FXCollections.observableArrayList();
        ObservableList<ObservableList<TechnologyExtended>> unlockedAndResearch = FXCollections.observableArrayList();
        unlockedAndResearch.add(unlocked);
        unlockedAndResearch.add(research);
        subscriber.subscribe(empireApiService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                empire -> {
                    if (empire.technologies() != null) {
                        for (String techId : empire.technologies()) {
                            subscriber.subscribe(getTechnology(techId),
                                    unlocked::add, error -> System.out.println("Error after try to get Technology " + techId + " because: " + error.getMessage()));
                        }
                    }
                    unlockedAndResearch.add(unlocked);
                    subscriber.subscribe(getTechnologies(),
                            techList -> {
                                for (TechnologyExtended tech : techList) {
                                    if (unlocked.stream().noneMatch(technology -> technology.id().equals(tech.id())) && research.stream().noneMatch(techEx -> techEx.id().equals(tech.id()))) {
                                        research.add(tech);
                                    }
                                }
                                unlockedAndResearch.add(research);
                            });
                }, error -> System.out.println("Error after try to get empire because of: " + error.getMessage()));
        return unlockedAndResearch;
    }

    // TOOD: MAYBE DO GETRESEACHANDUNLOCKED IN AN ARRAYLIST IN ONE METHOD
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