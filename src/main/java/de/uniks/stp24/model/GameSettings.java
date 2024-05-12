package de.uniks.stp24.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameSettings {

    int size;

    public GameSettings(){

    }

    public GameSettings(int size){
        this.size = size;
    }

    public int getSize(){
        return this.size;
    }
}
