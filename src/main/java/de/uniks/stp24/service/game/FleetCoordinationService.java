package de.uniks.stp24.service.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.GameFleetController;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import javafx.animation.*;
import javafx.collections.transformation.FilteredList;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.uniks.stp24.model.Fleets.Fleet;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


@Singleton
public class FleetCoordinationService {
    @Inject
    TokenStorage tokenStorage;
    @Inject
    FleetService fleetService;
    @Inject
    IslandsService islandsService;
    @Inject
    App app;

    private GameFleetController selectedFleet = null;
    private InGameController inGameController;
    private final Random random = new Random();
    private final Timeline pathAnimation = new Timeline();

    private final double ISLAND_RADIUS_X = (double) Constants.ISLAND_WIDTH/2;
    private final double ISLAND_RADIUS_Y = ((double) Constants.ISLAND_HEIGHT/2);

    @Inject
    public FleetCoordinationService() {
    }

    public void setInitialFleetPosition() {
        this.fleetService.onFleetCreated(this::putFleetOnMap);
        this.random.setSeed(Integer.parseInt(tokenStorage.getGameId().substring(0, 4), 16));

    }

    public void setFleet(GameFleetController fleet) {
        if (Objects.nonNull(this.selectedFleet)) {
            this.selectedFleet.activeCircle.setVisible(false);
            if (this.selectedFleet.equals(fleet)) this.selectedFleet = null;
            else this.selectedFleet = fleet;
        } else this.selectedFleet = fleet;
    }

    public void putFleetOnMap(Fleet fleet) {
        var island = this.islandsService.getIslandComponent(fleet.location());
        var gameFleet = this.app.initAndRender(new GameFleetController(fleet,this, this.fleetService));
        this.inGameController.setFleetOnMap(gameFleet);
        double angle = (random.nextInt(360)-90)*Math.PI/180;
        gameFleet.setLayoutX(island.getLayoutX() + ISLAND_RADIUS_X + ISLAND_RADIUS_X*Math.cos(angle));
        gameFleet.setLayoutY(island.getLayoutY() + ISLAND_RADIUS_Y + ISLAND_RADIUS_X*Math.sin(angle));
        gameFleet.collisionCircle.setRadius(Constants.FLEET_COLLISION_RADIUS);
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

     public void teleportFleet(MouseEvent mouseEvent) {
        if (Objects.nonNull(this.selectedFleet)) {
            this.selectedFleet.setLayoutX(mouseEvent.getX()-30);
            this.selectedFleet.setLayoutY(mouseEvent.getY()-30);
        }
    }

    /**
     * Mostly used for testing purposes. Can be removed after implementing is finished.
     */
    public void travelToMousePosition(MouseEvent mouseEvent) {
        if (Objects.isNull(this.selectedFleet)) return;
        this.selectedFleet.beginTravelAnimation(mouseEvent);
    }


    public ArrayList<String> getTravelPath(IslandComponent destinationIsland) {
        if (Objects.isNull(this.selectedFleet)) return null;
        Fleet currentFleet = this.selectedFleet.getFleet();

        // Find the shortest path using Dijkstra's algorithm
        List<String> islandIDs = this.islandsService.getListOfIslands().stream().map(Island::id).toList();
        List<String> visitedIslands = new ArrayList<>();
        Map<String, PathTableEntry> tableEntries = islandIDs.stream()
                .collect(toMap(identity(), id -> new PathTableEntry(id, this.islandsService.getConnections(id))));

        PathTableEntry currentIsland = tableEntries.get(destinationIsland.island.id());

        while (visitedIslands.size() != islandIDs.size()) {
            // Calculate the new shortest path for the connected islands
            if (Objects.nonNull(currentIsland)) {
                String currentIslandID = currentIsland.id;
                currentIsland.getConnections().forEach((id, dist) -> {
                    // Do nothing if the neighbor was already checked
                    if (visitedIslands.contains(id)) return;

                    PathTableEntry connectedIsland = tableEntries.get(id);
                    int prevDist = connectedIsland.getShortestPath();
                    // Do nothing if the neighbor's path is shorter than the new one
                    if (prevDist != -1 && prevDist < dist + prevDist) return;

                    connectedIsland.setShortestPath(prevDist + dist);
                    connectedIsland.setPreviousNode(currentIslandID);
                });
                visitedIslands.add(currentIsland.id);
            }

            // Get entry with the shortest path
            currentIsland = tableEntries.values().stream()
                    // Take only islands with a defined path
                    .filter(entry -> entry.shortestPath != -1)
                    // Take only islands that weren't visited
                    .filter(entry -> !visitedIslands.contains(entry.id))
                    // Considering previous statements, take island with the shortest path
                    .min(Comparator.comparing(PathTableEntry::getShortestPath)).orElse(null);
        }

        // Reconstruct the path
        ArrayList<String> path = new ArrayList<>();
        currentIsland = tableEntries.get(currentFleet.location());
        while (Objects.nonNull(currentIsland)) {
            path.add(currentIsland.id);
            currentIsland = tableEntries.get(currentIsland.getPreviousNode());
        }

        return path;
    }

    private static class PathTableEntry {
        private final Map<String, Integer> connections;
        private final String id;
        private int shortestPath;
        private String previousNode;

        public PathTableEntry(String id, Map<String, Integer> connections) {
            this.id = id;
            this.connections = connections;
            this.shortestPath = -1;
            this.previousNode = "";
        }

        public Map<String, Integer> getConnections() {
            return connections;
        }

        public int getShortestPath() {
            return shortestPath;
        }

        public void setShortestPath(int shortestPath) {
            this.shortestPath = shortestPath;
        }

        public String getPreviousNode() {
            return previousNode;
        }

        public void setPreviousNode(String previousNode) {
            this.previousNode = previousNode;
        }

        @Override
        public String toString() {
            return String.format("[X] Entry %s:\t Path: %d\t Previous: %s", id, shortestPath, previousNode);
        }
    }
}
