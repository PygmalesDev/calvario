package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.ExplainedVariableDTO;
import de.uniks.stp24.model.BuildingPresets;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.model.UpgradeStatus;
import de.uniks.stp24.service.VariablesTree;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class VariableDependencyService {
    @Inject
    VariableService variableService;

    @Inject
    public VariableDependencyService(){

    }

     /*
    Logic for making attributes of upgrades dependent from Variables
     */

    public SystemUpgrades createVariableDependencyUpgrades(SystemUpgrades systemUpgradeAttributes){
        UpgradeStatus upgraded = upgradeDependencyHandler(systemUpgradeAttributes, "upgraded");
        UpgradeStatus developed = upgradeDependencyHandler(systemUpgradeAttributes, "developed");
        UpgradeStatus colonized = upgradeDependencyHandler(systemUpgradeAttributes, "colonized");
        return new SystemUpgrades(systemUpgradeAttributes.unexplored(), systemUpgradeAttributes.explored(), colonized, upgraded, developed);
    }

    private UpgradeStatus upgradeDependencyHandler(SystemUpgrades systemUpgradeAttributes, String key){
        String id = null;
        float pop_growth = 0;
        Map<String, Integer> cost = new HashMap<>();
        Map<String, Integer> upkeep = new HashMap<>();
        float capacity_multiplier = 0;

        switch(key){
            case "colonized":
                id = systemUpgradeAttributes.colonized().id();
                pop_growth = (float) variableService.systemsTree.getNode("colonized", "pop_growth").getValue().finalValue();
                cost = castMapToInteger(createResourceMap(variableService.systemsTree.getNode("colonized", "cost").getChildren()));
                upkeep = castMapToInteger(createResourceMap(variableService.systemsTree.getNode("colonized", "upkeep").getChildren()));
                capacity_multiplier = (float) variableService.systemsTree.getNode("colonized", "capacity_multiplier").getValue().finalValue();
                break;
            case "upgraded":
                id = systemUpgradeAttributes.upgraded().id();
                pop_growth = (float) variableService.systemsTree.getNode("upgraded", "pop_growth").getValue().finalValue();
                cost = castMapToInteger(createResourceMap(variableService.systemsTree.getNode("upgraded", "cost").getChildren()));
                upkeep = castMapToInteger(createResourceMap(variableService.systemsTree.getNode("upgraded", "upkeep").getChildren()));
                capacity_multiplier = (float) variableService.systemsTree.getNode("upgraded", "capacity_multiplier").getValue().finalValue();
                break;
            case "developed":
                id = systemUpgradeAttributes.developed().id();
                pop_growth = (float) variableService.systemsTree.getNode("developed", "pop_growth").getValue().finalValue();
                cost = castMapToInteger(createResourceMap(variableService.systemsTree.getNode("developed", "cost").getChildren()));
                upkeep = castMapToInteger(createResourceMap(variableService.systemsTree.getNode("developed", "upkeep").getChildren()));
                capacity_multiplier = (float) variableService.systemsTree.getNode("developed", "capacity_multiplier").getValue().finalValue();
                break;
        }

        return new UpgradeStatus(id, pop_growth, cost, upkeep, capacity_multiplier);
    }

    /*
    Logic for making attributes of buildings (cost, upkeep, production) dependent from Variables
     */

    public void createVariableDependencyDistricts(){
        BuildingPresets buildingPresets;
        for(VariablesTree.Node<ExplainedVariableDTO> buildingNode: variableService.buildingsTree.getRoot().getChildren()){

        }
    }

    private BuildingPresets buildingDependencyHandler(VariablesTree.Node<ExplainedVariableDTO> buildingNode){
        String id = buildingNode.getKey();
        double build_time = variableService.buildingsTree.getNode(id, "build_time").getValue().finalValue();
        Map<String, Integer> cost;
        Map<String, Integer> upkeep;
        Map<String, Integer> production;
        return null;
    }

    /*
    Logic for making attributes of districts (cost, upkeep, production) dependent from Variables
     */

    public void createVariableDependencyBuildings(){

    }

    private Map<String, Double> createResourceMap(List<VariablesTree.Node<ExplainedVariableDTO>> children){
        Map<String, Double> result = new HashMap<>();
        for(VariablesTree.Node<ExplainedVariableDTO> node: children){
            result.put(node.getKey(), node.getValue().finalValue());
        }
        return result;
    }

    private Map<String, Integer> castMapToInteger(Map<String, Double> map){
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue().intValue());
        }
        return result;
    }
}
