package de.uniks.stp24.model;

import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.ShortSystemDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Contact {
    String empireFlag;
    String empireName;
    String empireID;
    final List<String> empireIslandsIDs = new ArrayList<>();
    final List<String> discoveredIslands = new ArrayList<>();
    final List<String> empireDtos = new ArrayList<>();

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
                  empireIslandsIDs.add(dto._id());
              }
          }
        );
        System.out.println("INFO ISLANDS");
        System.out.println(empireIslandsIDs.size());
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

}
