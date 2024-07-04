package de.uniks.stp24.service.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.ExplainedVariableDTO;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.service.InGameService;
import org.fulib.fx.controller.Subscriber;
import org.intellij.lang.annotations.JdkConstants;

import javax.inject.Inject;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class VariableService {
    @Inject
    InGameService inGameService;
    @Inject
    Subscriber subscriber;
    @Inject
    GameLogicApiService gameLogicApiService;

    public Map<String, ExplainedVariableDTO> data = new HashMap<>();
    private ArrayList<String> allVariables = new ArrayList<>();
    private InGameController inGameController;
    private  ArrayList<ExplainedVariableDTO> explainedVariableDTOS = new ArrayList<>();

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
                    updateVariablesMap();
                },
                error -> System.out.println("error in loading variable presets"));
    }

    /*
    This method should be called every time after a job is done.
     */
    public void updateVariablesMap(){
        this.subscriber.subscribe(
                gameLogicApiService.getVariablesExplanations(inGameController.tokenStorage.getEmpireId(), allVariables),
                result -> {
                    for(ExplainedVariableDTO explainedVariableDTO: result){
                        data.put(explainedVariableDTO.variable(), explainedVariableDTO);
                    }
                });
    }

    public ExplainedVariableDTO getValueOfVariable(String var){
        return data.get(var);
    }

    public void setIngameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
