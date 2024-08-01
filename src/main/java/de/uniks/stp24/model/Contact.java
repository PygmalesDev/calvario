package de.uniks.stp24.model;

import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.ShortSystemDto;

import java.util.*;

public class Contact {
    String empireFlag;
    String empireName;
    String empireID;
    final List<String> empireIslandsIDs = new ArrayList<>();
    final List<String> discoveredIslands = new ArrayList<>();
    final List<ShortSystemDto> empireDtos = new ArrayList<>();
    final Map<String, ShortSystemDto> mapEmpireDtos = new HashMap<>();

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
        if (!discoveredIslands.contains(id)) discoveredIslands.add(id);
    }

    public List<String> getDiscoveredIslands() {
        return Collections.unmodifiableList(this.discoveredIslands);
    }

    public void setEmpireDtos(List<ShortSystemDto> shorts) {
        System.out.println(shorts);
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
        System.out.println(empireDtos.size() + " -> " + empireDtos);
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

    public int getEmpirePopulation() {
        return empireDtos.stream().mapToInt(ShortSystemDto::population).sum();
    }

    public int getDiscoveredPopulation() {
        int discoveredPopulation = 0;
        for (String id : discoveredIslands) {
            ShortSystemDto tmp =  mapEmpireDtos.getOrDefault(id,null);
            if (tmp!=null) discoveredPopulation+=tmp.population();
        }
        return discoveredPopulation;
    }

    public double getIntel() {
        if(empireDtos.isEmpty()) return 0;
        return 100.0 * discoveredIslands.size()/empireDtos.size();
    }
}
