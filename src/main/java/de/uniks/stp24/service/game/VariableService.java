package de.uniks.stp24.service.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.ExplainedVariableDTO;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.VariablesTree;
import io.reactivex.rxjava3.core.Observable;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class VariableService {
    @Inject
    public InGameService inGameService;
    @Inject
    public Subscriber subscriber;
    @Inject
    GameLogicApiService gameLogicApiService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    public TechnologyService technologyService;

    private InGameController inGameController;
    public final ArrayList<String> allVariables = new ArrayList<>();
    public final Map<String, ExplainedVariableDTO> data = new HashMap<>();
    public VariablesTree<ExplainedVariableDTO> buildingsTree;
    public VariablesTree<ExplainedVariableDTO> districtsTree;
    public VariablesTree<ExplainedVariableDTO> systemsTree;
    public VariablesTree<ExplainedVariableDTO> empireTree;
    public VariablesTree<ExplainedVariableDTO> resourcesTree;
    public VariablesTree<ExplainedVariableDTO> technologiesTree;
    public ArrayList<Runnable> runnables = new ArrayList<>();


    @Inject
    public VariableService() {

    }

    public void dispose() {
        allVariables.clear();
        data.clear();
        buildingsTree = null;
        districtsTree = null;
        systemsTree = null;
        empireTree = null;
        resourcesTree = null;
        technologiesTree = null;
    }

    /*
    Initialize all existing variables from Server and saves it into list.
     */
    public void initVariables(){
        this.subscriber.subscribe(inGameService.getVariablesPresets(),
                result -> {
                    for (Map.Entry<String, Integer> entry : result.entrySet()) {
                        allVariables.add(entry.getKey());
                    }
                    loadVariablesDataStructure();
                },
                error -> System.out.println("error in loading variable presets"));
    }


    public void loadVariablesDataStructure(){
        this.subscriber.subscribe(
                this.getAllVariables(),
                result -> {
                    for (ExplainedVariableDTO explainedVariableDTO : result) {
                        data.put(explainedVariableDTO.variable(), explainedVariableDTO);
                    }
                    createAllTrees();
                    runRunnables();
                },
                error -> System.out.println("error in loading variable data structure:\n" + error.getMessage()));
    }

    public Observable<ArrayList<ExplainedVariableDTO>> getAllVariables(){
        return gameLogicApiService.getVariablesExplanations(inGameController.tokenStorage.getEmpireId(), allVariables);
    }

    public void createAllTrees(){
        buildingsTree = new VariablesTree<>("buildings");
        districtsTree = new VariablesTree<>("districts");
        systemsTree = new VariablesTree<>("systems");
        empireTree = new VariablesTree<>("empire");
        technologiesTree = new VariablesTree<>("technologies");
        resourcesTree = new VariablesTree<>("resources");

        createTree(buildingsTree);
        createTree(districtsTree);
        createTree(systemsTree);
        createTree(empireTree);
        createTree(technologiesTree);
        createTree(resourcesTree);
    }

    /*
    Logic for creating trees
     */

    public void createTree(VariablesTree<ExplainedVariableDTO> tree){
        for (Map.Entry<String, ExplainedVariableDTO> entry : data.entrySet()) {
            if(entry.getKey().contains(tree.getRoot().getKey())){
                String[] keysArray = entry.getKey().split("\\.");
                List<String> keys = new ArrayList<>();
                Collections.addAll(keys, keysArray);
                keys.removeFirst();
                createChildrenNodes(tree.getRoot(), keys, entry.getKey());
            }
        }
    }

    public void createChildrenNodes(VariablesTree.Node<ExplainedVariableDTO> currentNode, List<String> keys, String variable){
        if(!keys.isEmpty()){
            VariablesTree.Node<ExplainedVariableDTO> tmp = nodeExists(currentNode, keys.getFirst());
            if(tmp != null){
                keys.removeFirst();
                createChildrenNodes(tmp, keys, variable);
            } else {
                if(keys.size() == 1){
                    currentNode.addChild(new VariablesTree.Node<>(keys.getFirst(), data.get(variable)));
                    keys.removeFirst();
                } else {
                    VariablesTree.Node<ExplainedVariableDTO> newChild = new VariablesTree.Node<>(keys.getFirst());
                    currentNode.addChild(newChild);
                    keys.removeFirst();
                    createChildrenNodes(newChild, keys, variable);
                }
            }
        }
    }

    public VariablesTree.Node<ExplainedVariableDTO> nodeExists(VariablesTree.Node<ExplainedVariableDTO> currentNode, String key){
        for(VariablesTree.Node<ExplainedVariableDTO> node: currentNode.getChildren()){
            if(node.getKey().equals(key)){
                return node;
            }
        }
        return null;
    }

    public void setIngameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public Map<String, Double> convertVariablesToMap(ArrayList<ExplainedVariableDTO> variables) {
        Map<String, Double> variablesMap = new HashMap<>();
        for (ExplainedVariableDTO variableDTO : variables) {
            variablesMap.put(variableDTO.variable(), variableDTO.finalValue());
        }
        return variablesMap;
    }

    public void addRunnable(Runnable func) {
        runnables.add(func);
    }

    public void runRunnables() {
        runnables.forEach(Runnable::run);
    }
}
