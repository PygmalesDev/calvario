package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.model.SeasonComponent;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.WarsApiService;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class WarService {

    @Inject
    public  WarsApiService warsApiService;


    @Inject
    public WarService(){

    }

    public Observable<CreateWarDto> createWar(String gameID, CreateWarDto createWarDto) {
        return this.warsApiService.createWar(gameID, createWarDto);
    }

    public Observable<List<WarDto>> getWars(String gameID, String empireID) {
        return this.warsApiService.getWars(gameID, empireID);
    }

    public Observable<WarDto> getWar(String gameID, String warID) {
        return this.warsApiService.getWar(gameID, warID);
    }

    public Observable<UpdateWarDto> updateWar(String gameID, String warID, UpdateWarDto updateWarDto) {
        return this.warsApiService.updateWar(gameID, warID, updateWarDto);
    }

    public Observable<WarDto> deleteWar(String gameID, String warID) {
        return this.warsApiService.deleteWar(gameID, warID);
    }

}
