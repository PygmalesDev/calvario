package de.uniks.stp24.service.game;

import de.uniks.stp24.service.BasicService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SiteService extends BasicService {

    private String empireID;
    private final Map<String, Integer> capacities = new HashMap<>();

    @Inject
    public SiteService() {}

    public void setEmpireID(String id) {
        this.empireID = id;
    }

    public String getEmpireID() {
        return this.empireID;
    }

    public void putOrUpdateInfo(String key, int value) {
//        System.out.println("trying " );
        if (capacities.containsKey(key)) {
//            System.out.println("-> " + key + " " + value );
            capacities.compute(key,(k,v) -> Objects.nonNull(v) ? v + value : value );

        } else {
            capacities.put(key,value);
//            System.out.println("adding " + key + " " + value );
        }
//        System.out.println(capacities);
    }

    public int getCapacities(String siteID) {
//        System.out.println("getting " + siteID + " in " + capacities );
        int capacity = 0 ;
        for (String site : capacities.keySet()) {
            capacity += site.equals(siteID) ? capacities.get(site) : 0 ;
        }
        return capacity;
    }

    public int getTotalCapacity() {
        return capacities.values().stream().mapToInt(Integer::intValue).sum();
    }

}
