package de.uniks.stp24.model;

import de.uniks.stp24.component.game.ContactDetailsComponent;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.ShortSystemDto;

import java.util.*;

public class Contact {
    String empireFlag;
    String empireName;
    String empireID;
    String atIsland;
    String gameOwner;
    final List<String> empireIslandsIDs = new ArrayList<>();
    final List<String> discoveredIslands = new ArrayList<>();
    final List<ShortSystemDto> empireDtos = new ArrayList<>();
    final Map<String, ShortSystemDto> mapEmpireDtos = new HashMap<>();

    ContactDetailsComponent pane;

    public Contact(ReadEmpireDto dto) {
        this.empireID = dto._id();
        this.empireName = dto.name();
        this.empireFlag = "assets/flags/flag_"+dto.flag()+".png";
    }

    public String getEmpireID() { return this.empireID;}

    public String getEmpireName() {
        return this.empireName;
    }

    public String getEmpireFlag() {
        return this.empireFlag;
    }

    public void addIsland(String id) {
        this.atIsland = id;
        System.out.println("now at " + id.substring(20));
    if (!discoveredIslands.contains(id)) System.out.println(discoveredIslands.add(id));
    }

    public List<String> getDiscoveredIslands() {
        return Collections.unmodifiableList(this.discoveredIslands);
    }

    public void setEmpireDtos(List<ShortSystemDto> shorts) {
//        System.out.println(shorts);
        shorts.forEach(
          dto -> {
              if (dto.owner().equals(this.empireID) && !empireIslandsIDs.contains(dto._id())) {
                  mapEmpireDtos.putIfAbsent(dto._id(),dto);
                  // are these necessary?
                  empireIslandsIDs.add(dto._id());
                  empireDtos.add(dto);
                  System.out.println("health of this " + dto.health());
              }
          }
        );
        System.out.println("INFO ISLANDS");
        System.out.println(empireIslandsIDs.size() + " -> " + empireIslandsIDs);
//        System.out.println(empireDtos.size() + " -> " + empireDtos);
    }

    public void checkIslands() {
        if (discoveredIslands.isEmpty()) {
            discoveredIslands.addAll(this.getDiscoveredIslands());
        } else {
            this.getDiscoveredIslands().forEach(id -> {
                if (!discoveredIslands.contains(id)) {
                    discoveredIslands.add(id);
                }
            });
        }
    }

    public Map<String, Integer> getDiscoveryStats(){
        int discoveredPopulation = 0;
        int discoveredSites = 0;
        int discoveredBuildings = 0;
        for (String id : discoveredIslands) {
            ShortSystemDto tmp =  mapEmpireDtos.getOrDefault(id,null);
            if (tmp!=null) {
                discoveredPopulation += tmp.population();
                discoveredSites += tmp.districts().values().stream().mapToInt(Integer::intValue).sum();
                discoveredBuildings += tmp.buildings().size();
            }
        }
        return Map.of("pop", discoveredPopulation,
          "sites", discoveredSites,
          "buildings", discoveredBuildings);
    }

    public Map<String, Integer> getStatsAtLocation() {
        ShortSystemDto tmp =  mapEmpireDtos.getOrDefault(this.atIsland,null);
        return Map.of("pop" , tmp.population(),
          "sites",  tmp.districts().values().stream().mapToInt(Integer::intValue).sum(),
          "buildings", tmp.buildings().size());
    }

    public double getIntel() {
        if(empireDtos.isEmpty()) return 0;
        return 100.0 * discoveredIslands.size()/empireDtos.size();
    }

    public String getAtIsland() {
        return this.atIsland;
    }

    public void setPane(ContactDetailsComponent pane) {
        this.pane = pane;
    }

    public ContactDetailsComponent getPane() {
        return this.pane;
    }

    public void setGameOwner(String gameOwner) {
        this.gameOwner = gameOwner;
    }

    public String getGameOwner() {
        return this.gameOwner;
    }

    public void setStrength(double value) {
        System.out.println(value);
       this.pane.calculateStrength(value);
    }
}
