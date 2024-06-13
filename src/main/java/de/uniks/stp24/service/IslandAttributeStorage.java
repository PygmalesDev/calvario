package de.uniks.stp24.service;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.SystemUpgrades;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class IslandAttributeStorage {
    private EmpireDto empireDto;
    private SystemUpgrades systemPresets;
    private Island island;


    @Inject
    public IslandAttributeStorage() {

    }

    public void setEmpireDto(EmpireDto empire){
        empireDto = empire;
    }

    public Map<String, Integer> getNeededResources(int key) {
        switch(key){
            case 2:
                return systemPresets.colonized().cost();
            case 3:
                return systemPresets.developed().cost();
            case 4:
                return systemPresets.upgraded().cost();
        }
        return null;
    }

    public Map<String, Integer> getAvailableResources() {
        return empireDto.resources();
    }

    public void setSystemPresets(SystemUpgrades presets) {
        systemPresets = presets;
    }

    public void setIsland(Island island){
        this.island = island;
    }

    public Island getIsland(){
        return this.island;
    }

    public String[] getTech(){
        return empireDto.technologies();
    }

}
