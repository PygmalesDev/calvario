package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.UpdateEmpireDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResourcesService {
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    public EmpireService empireService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public Subscriber subscriber;

    private Map<String, Integer> neededResources;

    @Inject
    public ResourcesService() {
    }

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

    public boolean hasEnoughResources(Map<String, Integer> neededResources) {
        this.neededResources = neededResources;
        System.out.println(islandAttributes.getAvailableResources());
        System.out.println(neededResources);
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

    public Map<String, Integer> updateAvailableResources(Map<String, Integer> neededResources) {
        Map<String, Integer> difRes = new HashMap<>();
        for (Map.Entry<String, Integer> entry : neededResources.entrySet()) {
            String res = entry.getKey();
            int neededAmount = entry.getValue();
            difRes.put(res, -neededAmount);
        }
        return difRes;
    }


    public void upgradeIsland() {
        Map<String, Integer> difRes = updateAvailableResources(islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel()));

        this.subscriber.subscribe(empireService.updateEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId(),
                new UpdateEmpireDto(difRes, islandAttributes.getTech(), null, null, null)),
                result -> {
                    islandAttributes.setEmpireDto(result);
                    System.out.println("    Server -> minerals: " + result.resources().get("minerals") + " alloys: " + result.resources().get("alloys"));
                });

    }
}
