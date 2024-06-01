package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.EmpireApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static de.uniks.stp24.service.Constants.resourceTypes;

public class ResourcesService {
    @Inject
    EmpireApiService empireApiService;

    private final ObservableList<Resource> resourceList = FXCollections.observableArrayList();

    @Inject
    public ResourcesService() {}


    public ObservableList<Resource> generateResourceList(Map<String, Integer> resourceMap){
        for(Map.Entry<String, Integer> entry : resourceMap.entrySet()){
            if(entry.getValue() != null) {
                String resourceID = entry.getKey();
                int count = entry.getValue();
                int changeProSeason = 0;//Todo: getProSeason(name, count);
                Resource.ResourceType type = getResourceType(resourceID);
                Resource resource = new Resource(resourceID, count, changeProSeason, type);
                resourceList.add(resource);
            }
        }
        return this.resourceList;
    }

    public Resource.ResourceType getResourceType(String name){
        for(Map.Entry<Resource.ResourceType, List<String>> entry : resourceTypes.entrySet()){
            if(entry.getValue().contains(name)){
                return entry.getKey();
            }
        }
        return Resource.ResourceType.PRODUCTION;
    }
}
