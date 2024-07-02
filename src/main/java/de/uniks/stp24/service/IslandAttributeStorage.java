package de.uniks.stp24.service;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.BuildingPresets;
import de.uniks.stp24.model.DistrictPresets;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.SystemUpgrades;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.islandTranslation;

//Class can be used for getting Information of certain islands
@Singleton
public class IslandAttributeStorage {
    public EmpireDto empireDto;
    public SystemUpgrades systemPresets;
    public Island island;
    public ArrayList<BuildingPresets> buildings;
    public ArrayList<DistrictPresets> districts;
    public Map<Integer, String> upgradeEffects = new HashMap<>();

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;
    private int usedSlots;


    @Inject
    public IslandAttributeStorage() {

    }

    /*
    Methods for saving and getting certain inf. about selected island
     */

    public void setEmpireDto(EmpireDto empire) {
        empireDto = empire;
    }

    public void setSystemPresets(SystemUpgrades presets) {
        systemPresets = presets;

        String effectsColonized = "+" + systemPresets.colonized().pop_growth() * 100 + "% " + gameResourceBundle.getString("more.crewmates");
        String effectsUpgraded = "+" + systemPresets.upgraded().pop_growth() * 100 + "% " + gameResourceBundle.getString("more.crewmates");
        String effectsDeveloped = "+" + systemPresets.developed().pop_growth() * 100 + "% " + gameResourceBundle.getString("more.crewmates");

        String effectsColonizedCap;
        String effectsUpgradedCap;
        String effectsDevelopedCap;

        if(systemPresets.colonized().capacity_multiplier() != 1.0) {
            effectsColonizedCap = "+" + (systemPresets.colonized().capacity_multiplier() - 1) * 100 + "% " + gameResourceBundle.getString("more.capacity");
            effectsColonized = effectsColonized + "\n" + effectsColonizedCap;
        }
        if(systemPresets.upgraded().capacity_multiplier() != 1.0){
            effectsUpgradedCap = "+" + (systemPresets.upgraded().capacity_multiplier() - 1) * 100 + "% " + gameResourceBundle.getString("more.capacity");
            effectsUpgraded = effectsUpgraded + "\n" + effectsUpgradedCap;
        }
        if(systemPresets.developed().capacity_multiplier() != 1.0){
            effectsDevelopedCap = "+" + (systemPresets.developed().capacity_multiplier() - 1) * 100 + "% " + gameResourceBundle.getString("more.capacity");
            effectsDeveloped = effectsDeveloped + "\n" + effectsDevelopedCap;
        }

        upgradeEffects.put(1, null);
        upgradeEffects.put(2, effectsColonized);
        upgradeEffects.put(3, effectsUpgraded);
        upgradeEffects.put(4, effectsDeveloped);
    }

    public void setIsland(Island island) {
        this.island = island;
    }

    public Map<String, Integer> getNeededResources(int key) {
        return switch (key) {
            case 2 -> systemPresets.colonized().cost();
            case 3 -> systemPresets.upgraded().cost();
            case 4 -> systemPresets.developed().cost();
            default -> null;
        };
    }

    public Map<String, Integer> getUpkeep(int key) {
        return switch (key) {
            case 2 -> systemPresets.colonized().upkeep();
            case 3 -> systemPresets.upgraded().upkeep();
            case 4 -> systemPresets.developed().upkeep();
            default -> null;
        };
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

    public String getIslandNameTranslated(){
        return gameResourceBundle.getString(islandTranslation.get(island.type().name()));
    }

    public String getUpgradeTranslation(int lvl){
        String upgradeLvl = String.valueOf(Upgrade.values()[lvl]);
        return gameResourceBundle.getString(Constants.upgradeTranslation.get(upgradeLvl));
    }

    public void setUsedSlots(int slots){
        this.usedSlots = slots;
    }

    public int getUsedSlots(){
        return usedSlots;
    }
}

