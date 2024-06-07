package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.ResourceDto;
import de.uniks.stp24.dto.UpdateEmpireDto;
import de.uniks.stp24.rest.EmpireApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class EmpireService {
    @Inject
    EmpireApiService empireApiService;

    @Inject
    public  EmpireService() {}

    public Observable<EmpireDto> getEmpire(String gameID, String empireID){
        return this.empireApiService.getEmpire(gameID, empireID);
    }

    public Observable<ReadEmpireDto[]> getEmpires(String gameID){
        return this.empireApiService.getEmpires(gameID);
    }

    public Observable<EmpireDto> updateEmpire(String gameID, String empireID, UpdateEmpireDto updateEmpireDto){
        return this.empireApiService.updateEmpire(gameID, empireID, updateEmpireDto);
    }

    public Observable<List<ResourceDto>> getResources(){
        return this.empireApiService.getResources();
    }

}
