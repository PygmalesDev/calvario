package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.ResourceDto;
import de.uniks.stp24.dto.UpdateEmpireMarketDto;
import de.uniks.stp24.model.ExplainedVariable;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.rest.PresetsApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.Map;

public class MarketService {
    @Inject
    public PresetsApiService presetsApiService;
    @Inject
    EmpireApiService empireApiService;

    @Inject
    public MarketService() {
    }

    public Observable<Map<String, Integer>> getVariables(){
        return this.presetsApiService.getVariables();
    }

    public Observable<ResourceDto> getResources(){
        return this.presetsApiService.getResources();
    }

    public Observable<EmpireDto> getEmpire(String gameID, String empireID) {
        return this.empireApiService.getEmpire(gameID, empireID);
    }

    public Observable<UpdateEmpireMarketDto> updateEmpireMarket(String gameID, String empireID, UpdateEmpireMarketDto updateEmpireMarketDto) {
        return this.empireApiService.updateEmpireMarket(gameID, empireID, updateEmpireMarketDto);
    }

    //TODO create seasonal trades

    //TODO pause seasonal trades

    //TODO delete seasonal trades

}
