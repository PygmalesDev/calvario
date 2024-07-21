package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.Constants;
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
    public Subscriber subscriber;

    @Inject
    public TokenStorage tokenStorage;

    List<TechnologyExtended> technologies;

    Observable<ArrayList<TechnologyExtended>> temp;
    final Set<TechnologyExtended> allUnlockedTechnologiesSet = new HashSet<>();

    final ObservableList<TechnologyExtended> unlockedTechnologiesList = FXCollections.observableArrayList();

    ObservableList<TechnologyExtended> allUnlockedTechnologiesList = FXCollections.observableArrayList();

    @Inject
    public TechnologyService() {
    }

    public ObservableList<TechnologyExtended> getAllUnlockedTechnologies(String tag) {
        temp = getTechnologies();

        subscriber.subscribe(empireApiService.getEmpiresDtos(tokenStorage.getGameId()),
                empireDtos -> {
                    for (EmpireDto empireDto : empireDtos) {
                        if (empireDto.user().equals(tokenStorage.getUserId()) && empireDto.technologies() != null) {
                            for (String technology : empireDto.technologies()) {

                                subscriber.subscribe(getTechnology(technology),
                                        technologyExtended -> {
                                            ArrayList<String> tempTags = new ArrayList<>(Arrays.asList(technologyExtended.tags()));
                                            if (tempTags.contains(tag)) {
                                                allUnlockedTechnologiesSet.add(technologyExtended);
                                            }
                                        }
                                );

                            }
                        }
                    }
                },
                error -> System.out.println("Error when getting all unlocked Technologies: " + error)
        );
        allUnlockedTechnologiesList = FXCollections.observableArrayList(allUnlockedTechnologiesSet);
        return allUnlockedTechnologiesList;
    }

    public ObservableList<TechnologyExtended> getAllResearchTechnologies(String tag) {
        ObservableList<TechnologyExtended> research = FXCollections.observableArrayList();
        ObservableList<TechnologyExtended> unlocked = getAllUnlockedTechnologies(tag);

        technologies = temp.blockingFirst();
        for (TechnologyExtended technology : technologies) {
            if (!unlocked.contains(technology) && Arrays.asList(technology.tags()).contains(tag)) {
                research.add(technology);
            }
        }
        return research;
    }

    /**
     * iterate tru all unlocked Technologies independent of the tag and check if any of
     * their precedes are in temp, if yes add them in unlockedTechnologiesList
     *
     * @return unlocked Technologies without precedes that are also unlocked
     */
    public ObservableList<TechnologyExtended> getUnlockedTechnologies(String tag) {
        List<TechnologyExtended> tempUnlocked = getAllUnlockedTechnologies(tag);
        unlockedTechnologiesList.clear();
        for (TechnologyExtended technology : tempUnlocked) {
            for (String t : technology.precedes()) {

                if (tempUnlocked.stream().noneMatch(tech -> tech.id().equals(t))) {
                    unlockedTechnologiesList.add(technology);
                }
            }
        }
        return unlockedTechnologiesList;
    }

    /**
     * iterate tru all research Technologies and check their requirements
     * if all requirements are not in temp, add them in researchTechnologiesList
     *
     * @return research Technologies without requirements that are not unlocked
     */
    public ObservableList<TechnologyExtended> getResearchTechnologies(String tag) {
        List<TechnologyExtended> tempResearch = getResearchTechnologies();
        List<TechnologyExtended> tempUnlocked = getUnlockedTechnologies();
        ObservableList<TechnologyExtended> researchTechnologiesList = FXCollections.observableArrayList();

        for (TechnologyExtended technology : tempResearch) {
            boolean add = true;
            if (technology.requires() != null && !Arrays.asList(technology.requires()).isEmpty() && Arrays.asList(technology.tags()).contains(tag)) {
                for (String t : technology.requires()) {
                    if (tempUnlocked.stream().noneMatch(tech -> tech.id().equals(t))) {
                        add = false;
                        break;
                    }
                }
            }
            if (add && (Arrays.asList(technology.tags()).contains(tag)) && researchTechnologiesList.stream().noneMatch(tech -> tech.id().equals(technology.id()))) {
                researchTechnologiesList.add(technology);
            }
        }
        return researchTechnologiesList;
    }

    /**
     * get all unlocked Technologies independent of the tag
     */
    public ObservableList<TechnologyExtended> getUnlockedTechnologies() {
        ObservableList<TechnologyExtended> unlocked = FXCollections.observableArrayList();
        for (String tag : Constants.technologyTranslation.keySet()) {
            unlocked.addAll(getUnlockedTechnologies(tag));
        }
        return unlocked;
    }

    /**
     * get all research Technologies independent of the tag
     */
    public ObservableList<TechnologyExtended> getResearchTechnologies() {
        ObservableList<TechnologyExtended> research = FXCollections.observableArrayList();
        for (String tag : Constants.technologyTranslation.values()) {
            research.addAll(getAllResearchTechnologies(tag));
        }
        return research;
    }

    public Observable<TechnologyExtended> getTechnology(String id) {
        return presetsApiService.getTechnology(id);
    }

    public Observable<ArrayList<TechnologyExtended>> getTechnologies() {
        return presetsApiService.getTechnologies();
    }

}
