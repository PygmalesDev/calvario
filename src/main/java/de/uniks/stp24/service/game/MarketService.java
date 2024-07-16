package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.ResourceDto;
import de.uniks.stp24.dto.UpdateEmpireMarketDto;
import de.uniks.stp24.model.ExplainedVariable;
import de.uniks.stp24.model.SeasonComponent;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.rest.PresetsApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

//a5 Singleton so there is only one instance of MarketService
@Singleton
public class MarketService {
    @Inject
    public PresetsApiService presetsApiService;
    @Inject
    EmpireApiService empireApiService;

    private ObservableList<SeasonComponent> seasonComponents;

    @Inject
    public MarketService() {
    }

    public Observable<Map<String,Double>> getVariables(){
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

    //a2 set seasonComponents
    public void setSeasonComponents(ObservableList<SeasonComponent> seasonComponents) {
        this.seasonComponents = seasonComponents;
    }

    //a4 cancelSeasonalTrades by remoiving here. Important note. You have to do this in service. if you use this method
    //in MarketSEasonsComponent it componentCell will set itself null and thereby also the complete list.
    public void cancelSeasonalTrade(SeasonComponent seasonComponent){
        seasonComponents.remove(seasonComponent);
    }

    //TODO create seasonal trades

    //TODO pause seasonal trades

    //TODO delete seasonal trades

    public void dispose() {
        this.seasonComponents.clear();
    }

}
