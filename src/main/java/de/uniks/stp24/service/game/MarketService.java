package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.EmpirePrivate;
import de.uniks.stp24.dto.SeasonalTradeDto;
import de.uniks.stp24.dto.UpdateEmpireMarketDto;
import de.uniks.stp24.model.SeasonComponent;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class MarketService {
    @Inject
    public PresetsApiService presetsApiService;
    @Inject
    public EmpireApiService empireApiService;
    @Inject
    public Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;

    private ObservableList<SeasonComponent> seasonComponents = FXCollections.observableArrayList();

    @Inject
    public MarketService() {
    }

    public Observable<Map<String, Double>> getVariables() {
        return this.presetsApiService.getVariables();
    }

    public Observable<EmpireDto> getEmpire(String gameID, String empireID) {
        return this.empireApiService.getEmpire(gameID, empireID);
    }

    public Observable<UpdateEmpireMarketDto> updateEmpireMarket(String gameID, String empireID, UpdateEmpireMarketDto updateEmpireMarketDto) {
        return this.empireApiService.updateEmpireMarket(gameID, empireID, updateEmpireMarketDto);
    }

    public Observable<UpdateEmpireMarketDto> saveSeasonalComponents(String gameID, String empireID, SeasonalTradeDto seasonalTradeDto) {
        return this.empireApiService.saveSeasonalComponents(gameID, empireID, seasonalTradeDto);
    }

    public Observable<SeasonalTradeDto> getSeasonalTrades(String gameID, String empireID) {
        return this.empireApiService.getSeasonalTrades(gameID, empireID);
    }

    public void setSeasonComponents(ObservableList<SeasonComponent> seasonComponents) {
        this.seasonComponents = seasonComponents;
    }

    public void cancelSeasonalTrade(SeasonComponent seasonComponent) {
        seasonComponents.remove(seasonComponent);
        saveSeasonalTrades();
    }

    public void saveSeasonalTrades() {
        this.subscriber.subscribe(this.empireApiService.getPrivate(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()),
                result -> {
                    final Map<String, Object> newPrivateMap = Objects.nonNull(result._private()) ?
                            result._private() : new HashMap<>();
                    newPrivateMap.put("allSeasonalTrades", this.seasonComponents);
                    subscriber.subscribe(this.empireApiService.savePrivate(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(), new EmpirePrivate(newPrivateMap)),
                            saved -> {},
                            error -> System.out.println("error while saving fog: " + error.getMessage()));
                },
                error -> System.out.println("error while getting fog: " + error.getMessage())
        );
    }

    public void dispose() {
        this.seasonComponents.clear();
    }
}
