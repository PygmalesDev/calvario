package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.model.Resource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;

public class ResourcesService {

    @Inject
    public ResourcesService() {}

    public ObservableList<Resource> generateResourceList(Map<String, Integer> resourceMap, ObservableList<Resource> oldResourceList, AggregateItemDto[] aggregateItems){
        int i = 0;
        ObservableList<Resource> resourceList = FXCollections.observableArrayList();
        for(Map.Entry<String, Integer> entry : resourceMap.entrySet()){
            String resourceID = entry.getKey();
            int count = entry.getValue();
            int changeProSeason = 0;
            if(!oldResourceList.isEmpty()) {
                changeProSeason = oldResourceList.get(i).changePerSeason();
            }
            if(Objects.nonNull(aggregateItems)){
                changeProSeason = aggregateItems[i].subtotal();
            }
            Resource resource = new Resource(resourceID, count, changeProSeason);
            resourceList.add(resource);
            i++;
        }
        return resourceList;
    }

}
