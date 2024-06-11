package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.UpdateEmpireDto;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.TokenStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class ResourcesService {
    @Inject
    TokenStorage tokenStorage;
    @Inject
    EmpireService empireService;

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

    //TODO: YOU CANT WORK WITH "resoruces" here. Work with <String, Integer> or List<Resource>
    public boolean hasEnoughResources(Map<Resource, Integer> neededResources) {
        for (Map.Entry<Resource, Integer> entry : neededResources.entrySet()) {
            Resource resource = entry.getKey();
            int neededAmount = entry.getValue();
            int availableAmount = tokenStorage.getAvailableResource().get(resource);
            if (availableAmount < neededAmount) {
                return false;
            }
        }
        return true;
    }

    public void upgradeIsland(){
        if(hasEnoughResources(tokenStorage.getNeededResource())){
            empireService.updateEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId(),
                    new UpdateEmpireDto(updateAvailableResources(), tokenStorage.getTechnologies(), null, null, null)); //TODO: Change later !NULL
        }
    }

    //TODO: YOU CANT WORK WITH "resoruces" here. Work wit <String, Integer> or List<Resource>
    public Map<String, Integer> updateAvailableResources(){
        Map<String,Integer> newResourceList = new HashMap<>();
        for (Map.Entry<Resource, Integer> entry : tokenStorage.getNeededResource().entrySet()) {
            Resource resource = entry.getKey();
            int neededAmount = entry.getValue();
            int availableAmount = tokenStorage.getAvailableResource().get(resource);
            int newAmount = availableAmount - neededAmount;
            tokenStorage.getAvailableResource().put(resource, newAmount);
        }

        for (Map.Entry<Resource, Integer> entry : tokenStorage.getNeededResource().entrySet()) {
            newResourceList.put(entry.getKey().resourceID(), entry.getValue());
        }

        return newResourceList;
    }

}
