package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.rest.EmpireApiService;
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
    PresetsApiService presetsApiService;
    @Inject
    EmpireApiService empireApiService;
    @Inject
    public Subscriber subscriber;

    @Inject
    TokenStorage tokenStorage;

    Set<TechnologyExtended> unlockedTechnologiesSet = new HashSet<>();

    ObservableList<TechnologyExtended> researchTechnologiesList = FXCollections.observableArrayList();
    ObservableList<TechnologyExtended> unlockedTechnologiesList = FXCollections.observableArrayList();

    @Inject
    public TechnologyService() {
    }

    public ObservableList<TechnologyExtended> getUnlockedTechnologies(String category) {

        subscriber.subscribe(empireApiService.getEmpiresDtos(tokenStorage.getGameId()),
                empireDtos -> {
                    for (EmpireDto empireDto : empireDtos) {
                        if (empireDto.user().equals(tokenStorage.getUserId()) && empireDto.technologies() != null) {
                            for (String technology : empireDto.technologies()) {

                                subscriber.subscribe(getTechnology(technology),
                                        technologyExtended -> {
                                            ArrayList<String> tempTags = new ArrayList<>(Arrays.asList(technologyExtended.tags()));
                                            if (tempTags.contains(category)) {
                                                unlockedTechnologiesSet.add(technologyExtended);
                                            }
                                        }
                                );

                            }
                        }
                    }
                },
                Throwable::printStackTrace
        );
        unlockedTechnologiesList = FXCollections.observableArrayList(unlockedTechnologiesSet);
        return unlockedTechnologiesList;
    }

    public ObservableList<TechnologyExtended> getResearchTechnologies(String category) {

        getUnlockedTechnologies(category);
        subscriber.subscribe(presetsApiService.getTechnologies(),
                technologies -> {
                    for (TechnologyExtended technology : technologies) {
                        if (!unlockedTechnologiesList.contains(technology) && Arrays.asList(technology.tags()).contains(category)) {
                            researchTechnologiesList.add(technology);
                        }
                    }
                }
        );

        return researchTechnologiesList;
    }

    public Observable<TechnologyExtended> getTechnology(String id) {
        return presetsApiService.getTechnology(id);
    }

    public Observable<TechnologyExtended> getTechnologies() {
        return presetsApiService.getTechnologies().flatMap(Observable::fromIterable);
    }
}
