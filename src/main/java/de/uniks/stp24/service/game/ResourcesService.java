package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Resource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import java.util.Map;


public class ResourcesService {

    private Map<Resource, Integer> availableResources;
    private Map<Resource, Integer> neededResources;

    @Inject
    public ResourcesService(Map<Resource, Integer> availableResources) {
        this.availableResources = availableResources;
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

    public boolean hasEnoughResources(Map<Resource, Integer> neededResources) {
        for (Map.Entry<Resource, Integer> entry : neededResources.entrySet()) {
            Resource resource = entry.getKey();
            int neededAmount = entry.getValue();
            int availableAmount = availableResources.getOrDefault(resource, 0);
            if (availableAmount < neededAmount) {
                return false;
            }
        }
        return true;
    }

    public void upgradeIsland(){
        if(hasEnoughResources(neededResources)){
            //TODO: Sende Upgrade an den Server
        }
    }

    public void setNeededResources(Map<Resource, Integer> neededResources){
        this.neededResources = neededResources;
    }

}
