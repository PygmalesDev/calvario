package de.uniks.stp24.controllers.helper;

import java.util.HashMap;
import java.util.Map;

public class BattleEntry {
    public enum BATTLE_TYPE {
        EMPIRES,
        WILD
    }

    private final String attacker, defender, islandID;

    private BATTLE_TYPE battleType;

    private final Map<String, Integer> shipsLostByAttacker = new HashMap<>();
    private final Map<String, Integer> shipsLostByDefender = new HashMap<>();

    public BattleEntry(String attacker, String defender, String islandID) {
        this.attacker = attacker;
        this.defender = defender;
        this.islandID = islandID;
        this.battleType = BATTLE_TYPE.EMPIRES;
    }

    public BattleEntry(String attacker, String islandID) {
        this.attacker = attacker;
        this.defender = null;
        this.islandID = islandID;
        this.battleType = BATTLE_TYPE.WILD;
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

    public void addShipsLostByAttacker(String shipID) {

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

}
