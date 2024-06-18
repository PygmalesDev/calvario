package de.uniks.stp24.service;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.BuildingPresets;
import de.uniks.stp24.model.DistrictPresets;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.SystemUpgrades;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class IslandAttributeStorage {
    public EmpireDto empireDto;
    public SystemUpgrades systemPresets;
    public Island island;
    public ArrayList<BuildingPresets> buildings;
    public ArrayList<DistrictPresets> districts;


    @Inject
    public IslandAttributeStorage() {

    }

    public void setEmpireDto(EmpireDto empire) {
        empireDto = empire;
    }

    public void setSystemPresets(SystemUpgrades presets) {
        systemPresets = presets;
    }

    public void setIsland(Island island) {
        this.island = island;
    }

    //TODO: Just for Testing buildings page
    public void addNewBuilding() {
        island.buildings().add(String.valueOf(island.buildings().size()));
    }

    public Map<String, Integer> getNeededResources(int key) {
        switch (key) {
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

    public Island getIsland() {
        return this.island;
    }

    public String[] getTech() {
        return empireDto.technologies();
    }

    public void setBuildingPresets(ArrayList<BuildingPresets> buildings) {
        this.buildings = buildings;
    }

    public void setDistrictPresets(ArrayList<DistrictPresets> districts) {
        this.districts = districts;
    }

    public Map<String, Integer> getBuildingsProduction() {
        Map<String, Integer> buildingsProduction = new HashMap<>();
        for(String building: island.buildings()){
            int counter = 0;
            for(String tmp: island.buildings()){
                if(building.equals(tmp)){
                    counter++;
                }
            }
            for (BuildingPresets preset : buildings) {
                if(preset.id().equals(building)) {
                    for (Map.Entry<String, Integer> entry : preset.production().entrySet()) {
                        buildingsProduction.merge(entry.getKey(), entry.getValue() * counter, Integer::sum);
                    }
                }
            }
        }
        return buildingsProduction;
    }

    public Map<String, Integer> getDistrictProduction() {
        Map<String, Integer> sitesProduction = new HashMap<>();
        for(Map.Entry<String, Integer> entry : island.sites().entrySet()){
            for (DistrictPresets preset : districts) {
                if(preset.id().equals(entry.getKey())) {
                    for (Map.Entry<String, Integer> site : preset.production().entrySet()) {
                        sitesProduction.merge(site.getKey(), site.getValue() * entry.getValue(), Integer::sum);
                    }
                }
            }
        }
        return sitesProduction;
    }

    public Map<String, Integer> getBuildingsConsumption() {
        Map<String, Integer> buildingsConsumption = new HashMap<>();

        for(String building: island.buildings()){
            int counter = 0;
            for(String tmp: island.buildings()){
                if(building.equals(tmp)){
                    counter++;
                }
            }
            for (BuildingPresets preset : buildings) {
                if(preset.id().equals(building)) {
                    for (Map.Entry<String, Integer> entry : preset.upkeep().entrySet()) {
                        buildingsConsumption.merge(entry.getKey(), entry.getValue() * counter, Integer::sum);
                    }
                }
            }
        }
        return buildingsConsumption;
    }

    public Map<String, Integer> getDistrictConsumption() {
        Map<String, Integer> sitesConsumption = new HashMap<>();
        for(Map.Entry<String, Integer> entry : island.sites().entrySet()){
            for (DistrictPresets preset : districts) {
                if(preset.id().equals(entry.getKey())) {
                    for (Map.Entry<String, Integer> site : preset.upkeep().entrySet()) {
                        sitesConsumption.merge(site.getKey(), site.getValue() * entry.getValue(), Integer::sum);
                    }
                }
            }
        }
        return sitesConsumption;
    }

    public Map<String, Integer> mergeProduction(){
        Map<String, Integer> mergedMap = new HashMap<>(getBuildingsProduction());

        for (Map.Entry<String, Integer> entry : getDistrictProduction().entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            if (mergedMap.containsKey(key)) {
                mergedMap.put(key, mergedMap.get(key) + value);
            } else {
                mergedMap.put(key, value);
            }
        }
        return mergedMap;
    }

    public Map<String, Integer> mergeConsumption(){
        Map<String, Integer> mergedMap = new HashMap<>(getBuildingsConsumption());

        for (Map.Entry<String, Integer> entry : getDistrictConsumption().entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            if (mergedMap.containsKey(key)) {
                mergedMap.put(key, mergedMap.get(key) + value);
            } else {
                mergedMap.put(key, value);
            }
        }
        return mergedMap;
    }
}

