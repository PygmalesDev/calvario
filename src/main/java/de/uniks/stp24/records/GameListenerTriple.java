package de.uniks.stp24.records;

import de.uniks.stp24.model.Game;

import java.beans.PropertyChangeListener;

public record GameListenerTriple(Game game,
                         PropertyChangeListener listener,
                         String propertyName) {
}
