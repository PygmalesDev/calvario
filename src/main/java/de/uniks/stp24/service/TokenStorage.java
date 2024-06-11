package de.uniks.stp24.service;

import de.uniks.stp24.model.Resource;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.model.UpgradeStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TokenStorage {
    private String token;
    private String userId;
    private String name;
    private String avatar;
    private String gameId;
    private String empireId;
    private boolean isSpectator;
    private Map<String, Integer> flagsInGame = new HashMap<>();
    private SystemUpgrades systemPresets;
    private Map<Resource, Integer> neededResources;
    private Map<Resource, Integer> aviableResources;
    private String[] technologies;
    @Inject
    public TokenStorage() {
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getAvatar() {return avatar;}
    public void setAvatar(String avatar) {this.avatar = avatar;}

    public String getGameId() {return gameId;}
    public void setGameId(String gameId) {this.gameId = gameId;}

    public String getEmpireId() {return empireId;}
    public void setEmpireId(String empireId) {this.empireId = empireId;}

    public boolean isSpectator(){return this.isSpectator;}

    public void setIsSpectator(boolean isSpectator) {
        this.isSpectator = isSpectator;
    }

    public SystemUpgrades getSystemPresets(){
        return this.systemPresets;
    }
    public void setSystemPresets(SystemUpgrades presets){
        this.systemPresets = presets;
    }

    public Map<Resource, Integer> getNeededResource(){
        return this.neededResources;
    }
    public void setNeededResources(Map<Resource, Integer> resources){
        this.neededResources = resources;
    }

    public Map<Resource, Integer> getAvailableResource(){
        return this.aviableResources;
    }
    public void setAvailableResourcesResources(Map<Resource, Integer> resources){
        this.aviableResources = resources;
    }

    public String[] getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String[] tech){
        this.technologies = tech;
    }

    public void saveFlag(String id, int flagIndex) {
        System.out.println("new entry: " + id + " " + flagIndex);
        this.flagsInGame.put(id,flagIndex);
    }
    public int getFlagIndex(String id){
        return this.flagsInGame.getOrDefault(id,-1);
    }
}
