package de.uniks.stp24.service.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.UpdateEmpireDto;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class ResourcesService {
    @Inject
    IslandAttributeStorage islandAttributes;
    @Inject
    EmpireService empireService;
    @Inject
    TokenStorage tokenStorage;

    private Map<String, Integer> neededResources;

    @Inject
    public ResourcesService() {
   }


    public ObservableList<Resource> generateResourceList(Map<String, Integer> resourceMap, ObservableList<Resource> oldResourceList){
        int i = 0;
        ObservableList<Resource> resourceList = FXCollections.observableArrayList();
        for(Map.Entry<String, Integer> entry : resourceMap.entrySet()){
            if(entry.getValue() != null) {
                String resourceID = entry.getKey();
                int count = entry.getValue();
                int changeProSeason = 0;
                if(oldResourceList != null && !oldResourceList.isEmpty()) {
                    changeProSeason = oldResourceList.get(i).changePerSeason();//Todo: getProSeason(name, count);
                }
                Resource resource = new Resource(resourceID, count, changeProSeason);
                resourceList.add(resource);
                i++;
            } else {
                assert oldResourceList != null;
                if (entry.getKey().equals(oldResourceList.get(i).resourceID())) {
                    i++;
                }
            }
        }
        return resourceList;
    }

    public boolean hasEnoughResources(Map<String, Integer> neededResources) {
        this.neededResources = neededResources;
        for (Map.Entry<String, Integer> entry : neededResources.entrySet()) {
            String res = entry.getKey();
            int neededAmount = entry.getValue();
            int availableAmount = islandAttributes.getAvailableResources().get(res);
            if (availableAmount < neededAmount) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Integer> updateAvailableResources(Map<String, Integer> neededResources){
        for (Map.Entry<String, Integer> entry : neededResources.entrySet()) {
            String res = entry.getKey();
            int neededAmount = entry.getValue();
            int availableAmount = islandAttributes.getAvailableResources().get(res);
            int newAmount = availableAmount - neededAmount;
            islandAttributes.getAvailableResources().put(res, newAmount);
        }
        return islandAttributes.getAvailableResources();
    }


    public void upgradeIsland(){
        if(hasEnoughResources(neededResources)){
            empireService.updateEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId(),
                    new UpdateEmpireDto(updateAvailableResources(neededResources), islandAttributes.getTech(), null, null, null));
        }
    }

    //TODO: Do EventListener on hasEnoughResources. If true setz button Grün und Klickbar. If false setz button Rot nicht clickbar.

}
