package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.ExplainedVariableDTO;
import de.uniks.stp24.model.BuildingPresets;
import de.uniks.stp24.model.DistrictPresets;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.model.UpgradeStatus;
import de.uniks.stp24.service.VariablesTree;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class VariableDependencyService {
    @Inject
    VariableService variableService;

    @Inject
    public VariableDependencyService() {

    }

     /*
    Logic for making attributes of upgrades dependent from Variables
     */

    public SystemUpgrades createVariableDependencyUpgrades() {
        UpgradeStatus unexplored = upgradeDependencyHandler("unexplored");
        UpgradeStatus explored = upgradeDependencyHandler("explored");
        UpgradeStatus upgraded = upgradeDependencyHandler("upgraded");
        UpgradeStatus developed = upgradeDependencyHandler("developed");
        UpgradeStatus colonized = upgradeDependencyHandler("colonized");
        return new SystemUpgrades(unexplored, explored, colonized, upgraded, developed);
    }

    private UpgradeStatus upgradeDependencyHandler(String key) {
        float pop_growth = 0;
        Map<String, Integer> cost = new HashMap<>();
        Map<String, Integer> upkeep = new HashMap<>();
        float capacity_multiplier = 0;

        if (variableService.systemsTree.getNode(key, "pop_growth") != null) {
            pop_growth = (float) variableService.systemsTree.getNode(key, "pop_growth").getValue().finalValue();
        }

        if (variableService.systemsTree.getNode(key, "cost") != null) {
            cost = castMapToInteger(createResourceMap(variableService.systemsTree.getNode(key, "cost").getChildren()));
        }

        if (variableService.systemsTree.getNode(key, "upkeep") != null) {
            upkeep = castMapToInteger(createResourceMap(variableService.systemsTree.getNode(key, "upkeep").getChildren()));
        }

        if (variableService.systemsTree.getNode(key, "capacity_multiplier") != null) {
            capacity_multiplier = (float) variableService.systemsTree.getNode(key, "capacity_multiplier").getValue().finalValue();
        }

        return new UpgradeStatus(key, pop_growth, cost, upkeep, capacity_multiplier);
    }

    /*
    Logic for making attributes of buildings (cost, upkeep, production) dependent from Variables
     */

    public ArrayList<BuildingPresets> createVariableDependencyBuildings() {
        ArrayList<BuildingPresets> buildingsAttributes = new ArrayList<>();
        for (VariablesTree.Node<ExplainedVariableDTO> buildingNode : variableService.buildingsTree.getRoot().getChildren()) {
            buildingsAttributes.add(buildingDependencyHandler(buildingNode));
        }
        return buildingsAttributes;
    }

    private BuildingPresets buildingDependencyHandler(VariablesTree.Node<ExplainedVariableDTO> buildingNode) {
        String id = buildingNode.getKey();
        double build_time = variableService.buildingsTree.getNode(id, "build_time").getValue().finalValue();
        Map<String, Integer> cost = castMapToInteger(createResourceMap(variableService.buildingsTree.getNode(id, "cost").getChildren()));
        Map<String, Integer> upkeep = castMapToInteger(createResourceMap(variableService.buildingsTree.getNode(id, "upkeep").getChildren()));
        Map<String, Integer> production = new HashMap<>();

        if (variableService.buildingsTree.getNode(id, "production") != null) {
            production = castMapToInteger(createResourceMap(variableService.buildingsTree.getNode(id, "production").getChildren()));
        }
        return new BuildingPresets(id, build_time, cost, upkeep, production);
    }

    /*
    Logic for making attributes of districts (cost, upkeep, production) dependent from Variables
     */

    public ArrayList<DistrictPresets> createVariableDependencyDistricts() {
        ArrayList<DistrictPresets> districtsAttributes = new ArrayList<>();
        for (VariablesTree.Node<ExplainedVariableDTO> districtNode : variableService.districtsTree.getRoot().getChildren()) {
            districtsAttributes.add(districtsDependencyHandler(districtNode));
        }
        return districtsAttributes;
    }

    private DistrictPresets districtsDependencyHandler(VariablesTree.Node<ExplainedVariableDTO> districtNode) {
        String id = districtNode.getKey();
        double build_time = variableService.districtsTree.getNode(id, "build_time").getValue().finalValue();
        System.out.println(id);
        System.out.println(build_time);

        Map<String, Integer> chance = new HashMap<>();
        if(variableService.districtsTree.getNode(id, "chance") != null){
            chance = castMapToInteger(createResourceMap(variableService.districtsTree.getNode(id, "chance").getChildren()));
        }

        Map<String, Integer> cost = castMapToInteger(createResourceMap(variableService.districtsTree.getNode(id, "cost").getChildren()));
        Map<String, Integer> upkeep = castMapToInteger(createResourceMap(variableService.districtsTree.getNode(id, "upkeep").getChildren()));
        Map<String, Integer> production = castMapToInteger(createResourceMap(variableService.districtsTree.getNode(id, "production").getChildren()));

        return new DistrictPresets(id, build_time, chance, cost, upkeep, production);
    }

    private Map<String, Double> createResourceMap(List<VariablesTree.Node<ExplainedVariableDTO>> children) {
        Map<String, Double> result = new HashMap<>();
        for (VariablesTree.Node<ExplainedVariableDTO> node : children) {
            result.put(node.getKey(), node.getValue().finalValue());
        }
        return result;
    }

    private Map<String, Integer> castMapToInteger(Map<String, Double> map) {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue().intValue());
        }
        return result;
    }
}
