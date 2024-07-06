package de.uniks.stp24.service;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.BuildingPresets;
import de.uniks.stp24.model.DistrictPresets;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.service.game.VariableDependencyService;
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
    public SystemUpgrades systemUpgradeAttributes;
    public Island island;
    public ArrayList<BuildingPresets> buildingsAttributes;
    public ArrayList<DistrictPresets> districts;
    public Map<Integer, String> upgradeEffects = new HashMap<>();

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;
    private int usedSlots;
    @Inject
    VariableDependencyService variableDependencyService;


    @Inject
    public IslandAttributeStorage() {

    }

    /*
    Methods for saving and getting certain inf. about selected island
     */

    public void setEmpireDto(EmpireDto empire) {
        empireDto = empire;
    }

    public void setSystemUpgradeAttributes() {
        this.systemUpgradeAttributes = variableDependencyService.createVariableDependencyUpgrades();

        String effectsColonized = "+" + this.systemUpgradeAttributes.colonized().pop_growth() * 100 + "% " + gameResourceBundle.getString("more.crewmates");
        String effectsUpgraded = "+" + this.systemUpgradeAttributes.upgraded().pop_growth() * 100 + "% " + gameResourceBundle.getString("more.crewmates");
        String effectsDeveloped = "+" + this.systemUpgradeAttributes.developed().pop_growth() * 100 + "% " + gameResourceBundle.getString("more.crewmates");

        String effectsColonizedCap;
        String effectsUpgradedCap;
        String effectsDevelopedCap;

        if(this.systemUpgradeAttributes.colonized().capacity_multiplier() != 1.0) {
            effectsColonizedCap = "+" + (this.systemUpgradeAttributes.colonized().capacity_multiplier() - 1) * 100 + "% " + gameResourceBundle.getString("more.capacity");
            effectsColonized = effectsColonized + "\n" + effectsColonizedCap;
        }
        if(this.systemUpgradeAttributes.upgraded().capacity_multiplier() != 1.0){
            effectsUpgradedCap = "+" + (this.systemUpgradeAttributes.upgraded().capacity_multiplier() - 1) * 100 + "% " + gameResourceBundle.getString("more.capacity");
            effectsUpgraded = effectsUpgraded + "\n" + effectsUpgradedCap;
        }
        if(this.systemUpgradeAttributes.developed().capacity_multiplier() != 1.0){
            effectsDevelopedCap = "+" + (this.systemUpgradeAttributes.developed().capacity_multiplier() - 1) * 100 + "% " + gameResourceBundle.getString("more.capacity");
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
            case 2 -> systemUpgradeAttributes.colonized().cost();
            case 3 -> systemUpgradeAttributes.upgraded().cost();
            case 4 -> systemUpgradeAttributes.developed().cost();
            default -> null;
        };
    }

    public Map<String, Integer> getUpkeep(int key) {
        return switch (key) {
            case 2 -> systemUpgradeAttributes.colonized().upkeep();
            case 3 -> systemUpgradeAttributes.upgraded().upkeep();
            case 4 -> systemUpgradeAttributes.developed().upkeep();
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

    public void setBuildingPresets() {
        this.buildingsAttributes = variableDependencyService.createVariableDependencyBuildings();
    }

    public void setDistrictPresets() {
        this.districts = variableDependencyService.createVariableDependencyDistricts();
        System.out.println(districts);
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
            for (BuildingPresets preset : buildingsAttributes) {
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
            for (BuildingPresets preset : buildingsAttributes) {
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

