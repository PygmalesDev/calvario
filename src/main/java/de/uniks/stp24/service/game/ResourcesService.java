package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.model.Resource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Singleton
public class ResourcesService {

    /**
     * storage for actual resources
     */
    private Map<String, Integer> currentResources = new HashMap<>();

    @Inject
    public ResourcesService() {
    }

    public int getResourceCount(String resourceId) {
        return currentResources.get(resourceId);
    }

    /**
     * Updates the ObservableList which shows the count and change per season of a resource
     *
     * @param resourceMap:     map with new count of resources
     * @param oldResourceList: list with count and change per season before update
     * @param aggregateItems:  change per season for all resources; is null if there is no season change
     * @return updated ObservableList
     */

    public ObservableList<Resource> generateResourceList(Map<String, Integer> resourceMap, ObservableList<Resource> oldResourceList, AggregateItemDto[] aggregateItems) {
        currentResources = resourceMap;
        int i = 0;
        ObservableList<Resource> resourceList = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : resourceMap.entrySet()) {
            String resourceID = entry.getKey();
            int count = entry.getValue();
            int changeProSeason = 0;
            if (!oldResourceList.isEmpty()) {
                changeProSeason = oldResourceList.get(i).changePerSeason();
            }
            if (Objects.nonNull(aggregateItems)) {
                changeProSeason = aggregateItems[i].subtotal();
            }
            Resource resource = new Resource(resourceID, count, changeProSeason);
            resourceList.add(resource);
            i++;
        }
        return resourceList;
    }

}
