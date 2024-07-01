package de.uniks.stp24.service;

import java.util.HashMap;
import java.util.Map;

public class VariablesStorage {
    /*

    Speichere Variableninformationen hier.
    Speichere sie in einem Array, durchlaufe das Array und verlager die Informationen
    in eine Map mit übersetzter Variablen, sodass man die Map durchlaufen kann
    um die Variablen Kompoennete einzustellen.

     */

    private Map<String, Integer> variablesPresets = new HashMap<>();

    public void setVariablesPresets(){

    }

    public Map<String, Integer> getVariablesPresets(){
        return variablesPresets;
    }
}
