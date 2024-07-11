package de.uniks.stp24.service.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.ExplainedVariableDTO;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.VariablesTree;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

@Singleton
public class VariableService {
    @Inject
    public InGameService inGameService;
    @Inject
    public Subscriber subscriber;
    @Inject
    GameLogicApiService gameLogicApiService;

    public Map<String, ExplainedVariableDTO> data = new HashMap<>();
    private ArrayList<String> allVariables = new ArrayList<>();
    private InGameController inGameController;
    public VariablesTree<ExplainedVariableDTO> buildingsTree;
    public VariablesTree<ExplainedVariableDTO> districtsTree;
    public VariablesTree<ExplainedVariableDTO> systemsTree;
    public VariablesTree<ExplainedVariableDTO> empireTree;
    public VariablesTree<ExplainedVariableDTO> resourcesTree;
    public VariablesTree<ExplainedVariableDTO> technologiesTree;


    @Inject
    public VariableService(){

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
                gameLogicApiService.getVariablesExplanations(inGameController.tokenStorage.getEmpireId(), allVariables),
                result -> {
                    for (ExplainedVariableDTO explainedVariableDTO : result) {
                        data.put(explainedVariableDTO.variable(), explainedVariableDTO);
                    }
                    createAllTrees();
                    inGameController.loadGameAttributes();
                });
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
                for(String key: keysArray){
                    keys.add(key);
                }
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
}
