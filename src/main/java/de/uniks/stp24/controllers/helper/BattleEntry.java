package de.uniks.stp24.controllers.helper;

import de.uniks.stp24.model.Ships;
import de.uniks.stp24.model.Ships.Ship;

import java.util.HashMap;
import java.util.Map;

public class BattleEntry {
    public enum BATTLE_TYPE {
        EMPIRES,
        WILD
    }

    private final String attacker, defender, islandID;
    private String winnerID, loserID;

    private final Map<String, Integer> shipsLostByAttacker = new HashMap<>();
    private final Map<String, Integer> shipsLostByDefender = new HashMap<>();

    public BattleEntry(String attacker, String defender, String islandID) {
        this.attacker = attacker;
        this.defender = defender;
        this.islandID = islandID;
    }

    public BattleEntry(String attacker, String islandID) {
        this.attacker = attacker;
        this.defender = null;
        this.islandID = islandID;
    }

    public Map<String, Integer> getShipsLostByDefender() {
        return shipsLostByDefender;
    }

    public Map<String, Integer> getShipsLostByAttacker() {
        return shipsLostByAttacker;
    }

    public String getDefender() {
        return defender;
    }

    public String getAttacker() {
        return attacker;
    }

    public String getLocation() {
        return islandID;
    }

    public void setWinner(String winnerID) {
        this.winnerID = winnerID;
        this.loserID = attacker.equals(winnerID) ? defender : attacker;
    }

    public void addShipsLostByAttacker(String shipType) {
        if (!this.shipsLostByAttacker.containsKey(shipType)) this.shipsLostByAttacker.put(shipType, 0);
        this.shipsLostByAttacker.put(shipType, this.shipsLostByAttacker.get(shipType)+1);
    }

    public void addShipsLostByDefender(String shipType) {
        if (!this.shipsLostByDefender.containsKey(shipType)) this.shipsLostByDefender.put(shipType, 0);
        this.shipsLostByDefender.put(shipType, this.shipsLostByDefender.get(shipType)+1);
    }

    public boolean equals(String id1, String id2, String islandID, BATTLE_TYPE battleType) {
        switch (battleType) {
            case EMPIRES -> {
                return this.islandID.equals(islandID) && (id1.equals(attacker) && id2.equals(defender) ||
                        id1.equals(defender) && id2.equals(attacker));
            }
            case WILD -> {
                return this.attacker.equals(id1) && this.islandID.equals(islandID);
            }
            default -> {
                return false;
            }
        }
    }

    public boolean equals(String id1, String id2) {
        return id1.equals(attacker) && id2.equals(defender) || id1.equals(defender) && id2.equals(attacker);
    }

    public BattleEntry addShip(Ship ship) {
        if (ship.empire().equals(this.attacker))
            this.addShipsLostByAttacker(ship.type());
        else this.addShipsLostByDefender(ship.type());

        return this;
    }

    public String getWinner() {
        return winnerID;
    }

    public String getLoser() {
        return loserID;
    }
}
