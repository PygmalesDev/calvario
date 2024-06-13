package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.UpdateEmpireDto;
import de.uniks.stp24.model.Island;
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

public class ResourcesService {
    @Inject
    IslandAttributeStorage islandAttributes;
    @Inject
    EmpireService empireService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;

    private Map<String, Integer> neededResources;

    @Inject
    public ResourcesService() {
    }


    public ObservableList<Resource> generateResourceList(Map<String, Integer> resourceMap, ObservableList<Resource> oldResourceList) {
        int i = 0;
        ObservableList<Resource> resourceList = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : resourceMap.entrySet()) {
            if (entry.getValue() != null) {
                String resourceID = entry.getKey();
                int count = entry.getValue();
                int changeProSeason = 0;
                if (oldResourceList != null && !oldResourceList.isEmpty()) {
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

    public void updateAvailableResources(Map<String, Integer> neededResources) {
        for (Map.Entry<String, Integer> entry : neededResources.entrySet()) {
            String res = entry.getKey();
            int neededAmount = entry.getValue();
            int availableAmount = islandAttributes.getAvailableResources().get(res);
            int newAmount = availableAmount - neededAmount;
            islandAttributes.getAvailableResources().put(res, newAmount);
        }
        System.out.println(" Local changes -> minerals: " + islandAttributes.getAvailableResources().get("minerals") + " alloys: " + islandAttributes.getAvailableResources().get("alloys"));
    }


    public void upgradeIsland() {
        updateAvailableResources(islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel()));

        empireService.updateEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId(),
                new UpdateEmpireDto(islandAttributes.getAvailableResources(), islandAttributes.getTech(), null, null, null));

        //TODO
        this.subscriber.subscribe(empireService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                result -> {
                    islandAttributes.setEmpireDto(result);
                    System.out.println(" Server changes -> minerals: " + result.resources().get("minerals") + " alloys: " + result.resources().get("alloys"));
                });

    }

    public void resMapListener() {

    }
}
