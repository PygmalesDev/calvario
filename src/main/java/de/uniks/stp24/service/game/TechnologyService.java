package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
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

    Set<String> unlockedTechnologies = new HashSet<>();

    List<TechnologyExtended> researchTechnologiesList = new ArrayList<>();
    List<TechnologyExtended> unlockedTechnologiesList = new ArrayList<>();

    @Inject
    public TechnologyService() {
    }

    public List<TechnologyExtended> getUnlockedTechnologies() {
        subscriber.subscribe(empireApiService.getEmpiresDtos(tokenStorage.getGameId()),
                empireDtos -> {
                    for (EmpireDto empireDto : empireDtos) {
                        if (empireDto.user().equals(tokenStorage.getUserId()) && empireDto.technologies() != null) {
                            unlockedTechnologies.addAll(List.of(empireDto.technologies()));
                        }
                    }
                },
                Throwable::printStackTrace
        );

        subscriber.subscribe(presetsApiService.getTechnologies(),
                technologies -> {
                    for (TechnologyExtended technology : technologies) {
                        if (unlockedTechnologies.contains(technology.id())) {
                            unlockedTechnologiesList.add(technology);
                        } else {
                            System.out.println(technology);
                            System.out.println("Nicht freigeschaltet");
                            researchTechnologiesList.add(technology);
                        }
                    }
                },
                Throwable::printStackTrace
        );
        return unlockedTechnologiesList;
    }

    public List<TechnologyExtended> getResearchTechnologies() {
        getUnlockedTechnologies();
        return researchTechnologiesList;
    }



    public Observable<TechnologyExtended> getTechnologies() {
        return presetsApiService.getTechnologies().flatMap(Observable::fromIterable);
    }
}
