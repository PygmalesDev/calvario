package de.uniks.stp24.service.game;

import de.uniks.stp24.service.InGameService;
import org.fulib.fx.controller.Subscriber;
import org.intellij.lang.annotations.JdkConstants;

import javax.inject.Inject;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VariableService {
    @Inject
    InGameService inGameService;
    @Inject
    Subscriber subscriber;

    private ArrayList<String> allVariables = new ArrayList<>();

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
                    System.out.println(allVariables);
                },
                error -> System.out.println("error in loading variable presets"));
    }

    /*
    Filters list of Variables for needed Variables
     */

    /*
    Creates new data structure for chosen variable
     */
}
