package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.BattleResultComponent;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.controllers.helper.BattleEntry;
import de.uniks.stp24.controllers.helper.BattleEntry.BATTLE_TYPE;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Ships.Ship;
import de.uniks.stp24.service.TokenStorage;

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
    TokenStorage tokenStorage;

    BattleResultComponent battleResultComponent;

    @Inject
    public BattleService() {}

    private final List<BattleEntry> battles = new ArrayList<>();

    public void setBattleConditionUpdates() {
        this.fleetService.onFleetFled(this::onFleetFled);
        this.fleetService.onLoadingFinished(this::checkFleetPosition);
        this.fleetService.onFleetLocationChanged(this::checkFleetPosition);
        this.fleetService.onShipDestroyed(this::addDestroyedShip);
        this.fleetService.onFleetDestroyed(this::checkBattleConditionOnFleetDestroyed);
        this.contactsService.onWarDeleted(this::checkBattleConditionOnWarFinished);
    }

    public void checkFleetPosition(Fleet fleet) {
        if (Objects.isNull(fleet.empire())) return;

        IslandComponent islandComponent =  this.islandsService.getIslandComponent(fleet.location());
        Island location = islandComponent.island;

        if (Objects.nonNull(location.owner())) {
            if (isBattleStarted(fleet.empire(), location.owner(), location.id(), BATTLE_TYPE.EMPIRES)) {
                battles.add(new BattleEntry(fleet.empire(), location.owner(), location.id()));
                islandComponent.toggleSableVisibility(true);
            }
        } else {
            List<Fleet> otherFleets = this.fleetService.getFleetsOnIsland(location.id()).stream()
                    .filter(other -> Objects.isNull(other.empire()) || !other.empire().equals(fleet.empire())).toList();
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

    public void addDestroyedShip(Ship ship) {
        this.battles.stream().filter(battleEntry ->
                        isInBattle(battleEntry, ship.empire(), fleetService.getFleet(ship.fleet()).location()))
                .findFirst().map(battleEntry -> battleEntry.addShip(ship));
    }

    private void onFleetFled(Fleet oldFleet) {
        this.battles.stream().filter(battleEntry -> battleEntry.getLocation().equals(oldFleet.location()))
                .findFirst().map(battleEntry -> {
                    // TODO: PROVIDE TO WEBJAW fleetName has left the battle on islandName?

                    if (this.fleetService.getFleetsOnIsland(oldFleet.location())
                            .filtered(other -> oldFleet.empire().equals(other.empire())).isEmpty())
                        this.finishBattle(battleEntry);

                    return battleEntry;
                });
    }

    private void checkBattleConditionOnWarFinished(WarDto warDto) {
        this.battles.stream()
                .filter(battleEntry -> battleEntry.equals(warDto.attacker(), warDto.defender()))
                .forEach(this.battles::remove);
    }

    public void checkBattleConditionOnIslandClaimed(String islandID) {
        this.battles.stream().filter(battleEntry -> battleEntry.getLocation().equals(islandID))
                .findFirst().map(this::finishBattle);
    }

    public void checkBattleConditionOnFleetDestroyed(Fleet fleet) {
        this.battles.stream().filter(battleEntry -> isInBattle(battleEntry, fleet.empire(), fleet.location()))
                .findFirst().map(battleEntry -> {
                    if (this.fleetService.getFleetsOnIsland(fleet.location()).filtered(other ->
                            other.empire().equals(fleet.empire())).isEmpty()) {

                        if (this.tokenStorage.getEmpireId().equals(fleet.empire())) {
                            if (this.tokenStorage.getEmpireId().equals(battleEntry.getDefender()))
                                battleEntry.setWinner(battleEntry.getAttacker());
                            else battleEntry.setWinner(battleEntry.getDefender());
                        } else
                            battleEntry.setWinner(this.tokenStorage.getEmpireId());

                        if (Objects.isNull(this.islandsService.getIsland(battleEntry.getLocation()).owner()))
                            finishBattle(battleEntry);
                    }
                    return battleEntry;
                });
    }

    private BattleEntry finishBattle(BattleEntry battleEntry) {
        this.battles.remove(battleEntry);
        this.islandsService.getIslandComponent(battleEntry.getLocation())
                .toggleSableVisibility(false);

        this.battleResultComponent.setInfo(battleEntry);

        return battleEntry;
    }

    private boolean isInBattle(BattleEntry battleEntry, String empireID, String locationID) {
        return (battleEntry.getAttacker().equals(empireID) || battleEntry.getDefender().equals(empireID)) &&
        battleEntry.getLocation().equals(locationID);
    }

    private boolean isBattleStarted(String attackerID, String defenderID, String locationID, BATTLE_TYPE battleType) {
        return contactsService.areAtWar(attackerID, defenderID) &&
                battles.stream().noneMatch(battle -> battle.equals(locationID, defenderID, attackerID, battleType));
    }

    public void dispose() {
        this.battles.clear();
    }

    public void setBattleResultComponent(BattleResultComponent battleResultComponent) {
        this.battleResultComponent = battleResultComponent;
    }
}
