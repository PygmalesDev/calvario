package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.rest.GameLogicApiService;
import io.reactivex.rxjava3.core.Observable;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AggregateService {
    @Inject
    Subscriber subscriber;
    @Inject
    GameLogicApiService gameLogicApiService;
    public Observable<AggregateResultDto> getTechnologyCostAndTime(String empireID, String aggregate, String techID){
        return gameLogicApiService.getTechnologyCostAndTime(empireID, aggregate, techID);
    }
}
