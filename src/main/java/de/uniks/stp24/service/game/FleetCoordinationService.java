package de.uniks.stp24.service.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.GameFleetController;
import de.uniks.stp24.component.game.IslandClaimingComponent;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.controllers.helper.DistancePoint;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.utils.PathEntry;
import de.uniks.stp24.utils.PathTableEntry;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.*;

import static de.uniks.stp24.model.Fleets.Fleet;
import static de.uniks.stp24.service.Constants.FLEET_HW;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Singleton
public class FleetCoordinationService {
    @Inject
    TimerService timerService;
    @Inject
    JobsService jobsService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    FleetService fleetService;
    @Inject
    IslandsService islandsService;
    @Inject
    Subscriber subscriber;
    @Inject
    App app;

    private IslandClaimingComponent claimingComponent;
    private GameFleetController selectedFleet;
    private InGameController inGameController;
    private ObservableList<Node> debugGrid;
    private final Random random = new Random();
    private final List<PathEntry> pathEntries = new ArrayList<>();
    private final Map<GameFleetController, List<DistancePoint>> coordinatedPaths = new HashMap<>();

    private final double ISLAND_RADIUS_X = Constants.ISLAND_WIDTH/2;
    private final double ISLAND_RADIUS_Y = Constants.ISLAND_HEIGHT/2;
    private final int ROTATE_DURATION = 2;

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

        if (this.claimingComponent.getParent().isVisible())
            this.claimingComponent.setFleetInformation(this.selectedFleet);
    }

    public void putFleetOnMap(Fleet fleet) {
        var island = this.islandsService.getIslandComponent(fleet.location());
        var gameFleet = this.app.initAndRender(new GameFleetController(fleet,this, this.fleetService));

        if (Objects.nonNull(fleet.empire()))
            gameFleet.setEmpireColor(this.islandsService.getEmpire(fleet.empire()).color());

        this.inGameController.setFleetOnMap(gameFleet);
        double angle = (random.nextInt(360)-90)*Math.PI/180;
        gameFleet.setLayoutX(island.getLayoutX() + ISLAND_RADIUS_X + (ISLAND_RADIUS_X+Constants.FLEET_FROM_ISLAND_DISTANCE)*Math.cos(angle));
        gameFleet.setLayoutY(island.getLayoutY() + ISLAND_RADIUS_Y + (ISLAND_RADIUS_X+ Constants.FLEET_FROM_ISLAND_DISTANCE)*Math.sin(angle));
        gameFleet.setStartingPoint();
        gameFleet.collisionCircle.setRadius(Constants.FLEET_COLLISION_RADIUS);
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
        this.claimingComponent = inGameController.islandClaimingComponent;
        this.debugGrid = inGameController.debugGrid.getChildren();

        this.timerService.onGameTicked(this::processTravel);
        this.timerService.onSpeedChanged(this::processSpeedChanged);
    }

    public GameFleetController getSelectedFleet() {
        return selectedFleet;
    }

    private void processSpeedChanged() {
        this.coordinatedPaths.keySet().forEach(fleet -> {
            if (this.timerService.getServerSpeed() == 0) fleet.stopTravel();
            else fleet.travelToPoint(this.createSpeedChangedKeyframe(fleet), fleet.getCurrentPoint());
        });
    }

    private void processTravel() {
        this.coordinatedPaths.forEach((fleet, points) ->
            fleet.travelToPoint(this.createTravelKeyFrames(fleet, points.getFirst()), points.removeFirst())
        );
        this.coordinatedPaths.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private void processTravel(GameFleetController fleet) {
        fleet.travelToPoint(this.createTravelKeyFrames(fleet, this.coordinatedPaths.get(fleet).getFirst()),
                this.coordinatedPaths.get(fleet).removeFirst());
    }

    private List<KeyFrame> createTravelKeyFrames(GameFleetController fleet, DistancePoint nextPoint) {
        return List.of(
                new KeyFrame(Duration.seconds(ROTATE_DURATION),
                        new KeyValue(fleet.rotateProperty(), nextPoint.getPrev().angle(nextPoint)*360-95, Interpolator.EASE_BOTH),
                        new KeyValue(fleet.layoutXProperty(), nextPoint.getPrev().getX()-FLEET_HW, Interpolator.EASE_BOTH),
                        new KeyValue(fleet.layoutYProperty(), nextPoint.getPrev().getY()-FLEET_HW, Interpolator.EASE_BOTH)),

                new KeyFrame(Duration.seconds((60/ (double) this.timerService.getServerSpeed())-ROTATE_DURATION),
                        new KeyValue(fleet.layoutXProperty(), nextPoint.getX()-FLEET_HW, Interpolator.LINEAR),
                        new KeyValue(fleet.layoutYProperty(), nextPoint.getY()-FLEET_HW, Interpolator.LINEAR)));
    }

    private List<KeyFrame> createSpeedChangedKeyframe(GameFleetController fleet) {
        DistancePoint nextPoint = fleet.getCurrentPoint(), currentLocation = fleet.getCurrentLocation();
        double distance = Math.abs(nextPoint.distance(nextPoint.getPrev())),
               traveledDistance = currentLocation.distance(nextPoint.getPrev()),
               timeDif = 60-60*(traveledDistance/distance);

        System.out.println("Traveled Distance:" + traveledDistance);
        System.out.println("Absolute Distance:" + distance);
        System.out.println(traveledDistance/distance);
        System.out.println();

        return List.of(
                new KeyFrame(Duration.seconds(timeDif/this.timerService.getServerSpeed()),
                new KeyValue(fleet.layoutXProperty(), nextPoint.getX()-FLEET_HW, Interpolator.LINEAR),
                new KeyValue(fleet.layoutYProperty(), nextPoint.getY()-FLEET_HW, Interpolator.LINEAR))
        );
    }

    public void travelToIsland(Island destinationIsland) {
        if (Objects.isNull(this.selectedFleet)) return;

        // The path entry should be generated at this point!
        PathEntry entry = this.getPathEntry(this.selectedFleet.getFleet().location(), destinationIsland.id());
        List<DistancePoint> coordinatedPath = this.getCoordinatedPath(entry);
        this.processTravel(this.selectedFleet);

        this.debugGrid.clear();
        for (int i = 0; i < coordinatedPath.size()-1; i++) {
            Circle circle = new Circle(coordinatedPath.get(i).getX(), coordinatedPath.get(i).getY(), 10);
            circle.setFill(Color.RED);
            this.debugGrid.add(circle);
            Line line = new Line(coordinatedPath.get(i).getX(), coordinatedPath.get(i).getY(),
                    coordinatedPath.get(i+1).getX(), coordinatedPath.get(i+1).getY());
            line.getStyleClass().add("connectionPath");
            this.debugGrid.add(line);
        }

//        this.subscriber.subscribe(this.fleetService.beginTravelJob(path, this.selectedFleet.getFleet()._id()),
//                job -> {
//                    System.out.println(job.total());
//                    this.selectedFleet.beginTravelAnimation(this.getCoordinatedPath(path));
//                },
//                error -> System.out.println("Caught an exception while trying to create a new travel job in the" +
//                        "FleetCoordinationService:\n" + error.getMessage()));
    }

    private List<DistancePoint> getCoordinatedPath(PathEntry pathEntry) {
        ArrayList<String> path = pathEntry.getPathFromLocation(this.selectedFleet.getFleet().location());
        List<DistancePoint> coordinatedPath = new ArrayList<>();

        // Get the needed total number of points that need to be put between the islands
        byte interPoints = (byte) (this.getTravelDuration(pathEntry) - (path.size()-1));
        // Get the number of points that should be put between two islands
        byte pointAlloc  = (byte) (interPoints/(path.size()-1));

        coordinatedPath.add(new DistancePoint(
                this.islandsService.getIslandComponent(path.getFirst()).getLayoutX() + ISLAND_RADIUS_X + FLEET_HW,
                this.islandsService.getIslandComponent(path.getFirst()).getLayoutY() + ISLAND_RADIUS_Y + FLEET_HW,
                null));

        // Put the intermediate points first, then the island location
        for (int i = 0; i < path.size()-1; i++) {
            // Intermediate points
            List<Point2D> distancePoints = this.islandsService.generateDistancePoints(path.get(i), path.get(i+1), pointAlloc);
            distancePoints.forEach(point -> coordinatedPath.add(new DistancePoint(point.getX(), point.getY(), coordinatedPath.getLast())));

            // Island location
            coordinatedPath.add(new DistancePoint(
                    this.islandsService.getIslandComponent(path.get(i+1)).getLayoutX() + ISLAND_RADIUS_X + FLEET_HW,
                    this.islandsService.getIslandComponent(path.get(i+1)).getLayoutY() + ISLAND_RADIUS_Y + FLEET_HW,
                    coordinatedPath.getLast()));
        }

        coordinatedPath.removeFirst();
        this.coordinatedPaths.put(this.selectedFleet, coordinatedPath);
        return coordinatedPath;
    }

    public void generateTravelPaths(Island destinationIsland) {
        // Find the shortest path using Dijkstra's algorithm
        if (Objects.isNull(this.selectedFleet)) return;
        String startLocation = this.selectedFleet.getFleet().location();
        String endLocation = destinationIsland.id();

        // Return the travel path if it already was computed
        PathEntry calculatedPath = this.getPathEntry(startLocation, endLocation);
        if (Objects.nonNull(calculatedPath)) return;

        List<String> islandIDs = this.islandsService.getListOfIslands().stream().map(Island::id).toList();
        List<String> visitedIslands = new ArrayList<>();
        Map<String, PathTableEntry> tableEntries = islandIDs.stream()
                .collect(toMap(identity(), id -> new PathTableEntry(id, this.islandsService.getConnections(id))));

        PathTableEntry currentIsland = tableEntries.get(startLocation);

        while (visitedIslands.size() != islandIDs.size()) {
            // Calculate the new shortest path for the connected islands
            if (Objects.nonNull(currentIsland)) {
                String currentIslandID = currentIsland.getID();
                int currentShortestPath = currentIsland.getShortestPath();
                currentIsland.getConnections().forEach((id, dist) -> {
                    // Do nothing if the neighbor was already checked
                    if (visitedIslands.contains(id)) return;

                    PathTableEntry connectedIsland = tableEntries.get(id);
                    int prevDist = connectedIsland.getShortestPath();
                    // Do nothing if the neighbor's path is shorter than the new one
                    if (prevDist != -1 && prevDist < dist + currentShortestPath) return;

                    connectedIsland.setShortestPath(currentShortestPath + dist);
                    connectedIsland.setPreviousNode(currentIslandID);
                });
                visitedIslands.add(currentIsland.getID());

                // Stop iterating if the island of interest was already found
                if (currentIslandID.equals(endLocation)) break;
            }

            // Get entry with the shortest path
            currentIsland = tableEntries.values().stream()
                    // Take only islands with a defined path
                    .filter(entry -> entry.getShortestPath() != -1)
                    // Take only islands that weren't visited
                    .filter(entry -> !visitedIslands.contains(entry.getID()))
                    // Considering previous statements, take island with the shortest path
                    .min(Comparator.comparing(PathTableEntry::getShortestPath)).orElse(null);
        }

        // Reconstruct the paths for all traversed islands and save them for future use
        visitedIslands.removeFirst();
        visitedIslands.forEach(id -> {
            ArrayList<String> path = new ArrayList<>();
            PathTableEntry tableEntry = tableEntries.get(id);
            int distance = tableEntry.getShortestPath();
            while (Objects.nonNull(tableEntry)) {
                path.add(tableEntry.getID());
                tableEntry = tableEntries.get(tableEntry.getPreviousNode());
            }
            PathEntry entry = new PathEntry(path, distance);
            if (!this.pathEntries.contains(entry)) this.pathEntries.add(entry);
        });
    }

    /**
     * Returns the PathEntry, containing the shortest path between two islands counting them.
     * The order in which the island IDs are provided doesn't matter. <p>
     * The traveling path will be generated automatically by pressing on an island with selected fleet.
     * @param startIslandID ID of the first island
     * @param endIslandID ID of the second island
     * @return PathEntry with the shortest path between two islands
     */
    public PathEntry getPathEntry(String startIslandID, String endIslandID) {
        return this.pathEntries.stream().filter(entry -> entry.equals(startIslandID, endIslandID))
                .findFirst().orElse(null);
    }

    /**
     * Returns the travel duration between two islands, calculated from the travel distance and the speed of
     * the slowest ship in the fleet. The order in which the island IDs are provided doesn't matter.
     * @param startIslandID ID of the first island
     * @param endIslandID ID of the second island
     * @return Rounded number of the seasons needed for this travel
     */
    public int getTravelDuration(String startIslandID, String endIslandID) {
        //TODO: Get the speed of the slowest ship in the fleet from some other service!
        return (int) Math.ceil((double) this.getPathEntry(startIslandID, endIslandID).getDistance()/5);
    }

    public int getTravelDuration(PathEntry entry) {
        return (int) Math.ceil((double) entry.getDistance()/5);
    }

    public void dispose() {
        this.selectedFleet = null;
        this.subscriber.dispose();
        this.coordinatedPaths.clear();
        this.pathEntries.clear();
    }
}
