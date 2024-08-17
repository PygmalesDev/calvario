package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.controllers.helper.BattleEntry;
import de.uniks.stp24.controllers.helper.BattleEntry.BATTLE_TYPE;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Island;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class BattleService {
    @Inject
    FleetService fleetService;
    @Inject
    IslandsService islandsService;
    @Inject
    ContactsService contactsService;

    @Inject
    public BattleService() {}

    private final List<BattleEntry> battles = new ArrayList<>();

    public void setFleetLocationUpdates() {
        // After a fleet reaches an island, check for island owner and for fleets that are on this island
        // (Additionally for wars)

        this.fleetService.onFleetLocationChanged(this::checkFleetPosition);

        // Fall1: Fleet meets enemy island
        // Fall2: enemy fleet meets your island
        // Fall3: two fleets meet at an uncolonized island
        // Fall4: wild fleets
    }

    public void checkFleetPosition(Fleet fleet) {
        IslandComponent islandComponent =  this.islandsService.getIslandComponent(fleet.location());
        Island location = islandComponent.island;

        if (Objects.nonNull(location.owner())) {
            if (isBattleStarted(fleet._id(), location.owner(), location.id(), BATTLE_TYPE.EMPIRES)) {
                battles.add(new BattleEntry(fleet.empire(), location.owner(), location.id()));
                islandComponent.toggleSableVisibility(true);
            }
        } else {
            List<Fleet> otherFleets = this.fleetService.getFleetsOnIsland(location.id()).stream()
                    .filter(other -> other.empire().equals(fleet.empire())).toList();
            otherFleets.stream().map(Fleet::empire).collect(Collectors.toSet()).forEach(empireID -> {
                if (Objects.nonNull(empireID)) {
                    if (isBattleStarted(fleet.empire(), empireID, location.id(), BATTLE_TYPE.EMPIRES)) {
                        battles.add(new BattleEntry(fleet.empire(), location.owner(), location.id()));
                        islandComponent.toggleSableVisibility(true);
                    }
                } else {
                    if (isBattleStarted(fleet.empire(), null, location.id(), BATTLE_TYPE.WILD)) {
                        battles.add(new BattleEntry(fleet.empire(), location.id()));
                        islandComponent.toggleSableVisibility(true);
                    }
                }
            });
        }
    }

    private boolean isBattleStarted(String attackerID, String defenderID, String locationID, BATTLE_TYPE battleType) {
        return contactsService.areAtWar(attackerID, defenderID) &&
                battles.stream().noneMatch(battle -> battle.equals(locationID, defenderID, attackerID, battleType));
    }

    public void dispose() {

    }
}
