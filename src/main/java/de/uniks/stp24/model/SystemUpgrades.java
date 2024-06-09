package de.uniks.stp24.model;

import de.uniks.stp24.dto.Upgrade;

public record SystemUpgrades(
        UpgradeStatus unexplored,
        UpgradeStatus explored,
        UpgradeStatus colonized,
        UpgradeStatus upgraded,
        UpgradeStatus developed
) {
}
