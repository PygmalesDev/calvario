package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.LoginResult;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.EmpireApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static de.uniks.stp24.service.Constants.resourceTypes;

public class ResourcesService {
    @Inject
    EmpireApiService empireApiService;

    private List<Resource> resourceList;

    @Inject
    public ResourcesService() {}


    public List<Resource> generateResourceList(Map<String, Integer> resourceMap){
        for(Map.Entry<String, Integer> entry : resourceMap.entrySet()){
            String resourceID = entry.getKey();
            int count = entry.getValue();
            int changeProSeason = 0;///getProSeason(name, count);
            Resource.ResourceType type = getResourceType(resourceID);
            Resource resource = new Resource(resourceID, count, changeProSeason, type);
            resourceList.add(resource);
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
