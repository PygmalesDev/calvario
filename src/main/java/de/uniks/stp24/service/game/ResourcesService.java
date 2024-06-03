package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Resource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import java.util.Map;


public class ResourcesService {

    @Inject
    public ResourcesService() {}


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

    public ObservableList<Resource> updateChangePerSeason(ObservableList<Resource> items, Map<String, Integer> resourcesLastSeasonChange){
        return null;
    }

}
