package de.uniks.stp24.service.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.GameFleetController;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.controllers.helper.DistancePoint;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.utils.PathEntry;
import de.uniks.stp24.utils.PathTableEntry;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Duration;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.*;
import java.util.function.Consumer;

import static de.uniks.stp24.model.Fleets.Fleet;
import static de.uniks.stp24.service.Constants.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * A service that coordinates fleets position on the map and their traveling paths.
 * Contains methods for path generation and saving, as well as animation processing.
 */
@Singleton
public class FleetCoordinationService {
    @Inject
    TimerService timerService;
    @Inject
    JobsService jobsService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public FleetService fleetService;
    @Inject
    IslandsService islandsService;
    @Inject
    public ImageCache imageCache;
    @Inject
    public Subscriber subscriber;
    @Inject
    public ShipService shipService;
    @Inject
    App app;

    public final Random random = new Random();
    private GameFleetController selectedFleet;
    private ObservableList<Node> mapGrid;
    private final List<PathEntry> pathEntries = new ArrayList<>();
    private final Map<GameFleetController, List<DistancePoint>> coordinatedPaths = new HashMap<>();
    private ObservableList<Job> travelJobs = FXCollections.observableArrayList();
    private final List<Consumer<Fleet>> onFleetSelectedConsumers = new ArrayList<>();
    private final List<GameFleetController> enemyFleets = new ArrayList<>();

    private final int ROTATE_DURATION = 2;

    @Inject
    public FleetCoordinationService() {
    }

    public void setInitialFleetPosition() {
        this.random.setSeed(Integer.parseInt(this.tokenStorage.getGameId().substring(0, 4), 16));

        this.jobsService.onJobsLoadingFinished(() -> {
            this.travelJobs = this.jobsService.getJobObservableListOfType("travel");
            this.travelJobs.forEach(job -> {
                this.setOnJobDeletion(job);
                this.setOnJobCompletion(job);
            });
        });

        this.fleetService.onLoadingFinished(() ->
                this.fleetService.getGameFleets().forEach(this::putFleetOnMap));

        this.fleetService.onFleetCreated(this::putFleetOnMap);
        this.fleetService.onFleetDestroyed(this::deleteFleetFromMap);
        this.fleetService.onFleetLocationChanged(this::processTravelForEnemyFleets);
    }

    private void setOnJobDeletion(Job job) {
        this.jobsService.onJobDeletion(job._id(), () ->
            this.getTravelingFleet(job.fleet())
                    .map(gameFleet -> {
                        this.coordinatedPaths.remove(gameFleet);
                        gameFleet.setFleet(this.fleetService.getFleet(job.fleet()));
                        this.processReturn(gameFleet);
                        return gameFleet;
                    }).orElseThrow()
        );
    }

    private void setOnJobCompletion(Job job) {
        this.jobsService.onJobCompletion(job._id(), () ->
           this.getTravelingFleet(job.fleet())
                    .map(gameFleet -> {
                        this.coordinatedPaths.remove(gameFleet);
                        gameFleet.setFleet(this.fleetService.getFleet(job.fleet()));
                        this.processFinish(gameFleet, this.islandsService.getIslandComponent(job.path().getLast()));
                        return gameFleet;
                    }).orElseThrow()
        );
    }

    private Optional<GameFleetController> getTravelingFleet(String fleetID) {
        return  this.coordinatedPaths.keySet().stream()
                .filter(gameFleet -> gameFleet.getFleet()._id().equals(fleetID))
                .findFirst();
    }

    public void setFleet(GameFleetController fleet) {
        if (Objects.nonNull(this.selectedFleet)) {
            this.selectedFleet.toggleActive();
            if (this.selectedFleet.equals(fleet)) this.selectedFleet = null;
            else {
                this.selectedFleet = fleet;
                fleet.toggleActive();
            }
        } else {
            this.selectedFleet = fleet;
            fleet.toggleActive();
        }

        this.onFleetSelectedConsumers.forEach(func -> func.accept(this.getSelectedFleet()));
    }

    private void deleteFleetFromMap(Fleet fleet) {
        this.mapGrid.removeIf(node -> {
            if (node instanceof GameFleetController fleetController)
                return (fleetController.getFleet().equals(fleet));
            return false;
        });
    }

    public void putFleetOnMap(Fleet fleet) {
        var gameFleet = this.app.initAndRender(new GameFleetController(fleet,this));

        if (Objects.nonNull(fleet.empire())) {
            gameFleet.renderWithColor(this.islandsService.getEmpire(fleet.empire()).color());
            if (!fleet.empire().equals(this.tokenStorage.getEmpireId())) this.enemyFleets.add(gameFleet);
        } else gameFleet.renderWithColor("white");

        this.mapGrid.add(gameFleet);
        this.selectedFleet = gameFleet;

        Optional<Job> jobOptional = this.travelJobs.stream().filter(job -> job.fleet().equals(fleet._id())).findFirst();
        if (jobOptional.isPresent() && jobOptional.get().progress() != jobOptional.get().total()) {
            // If fleet had a travel job, put fleet on the corresponding travel progress location and continue the travel
            Job travelJob = jobOptional.get();
            this.generateTravelPaths(travelJob.path().getFirst(), travelJob.path().getLast());
            PathEntry entry = this.getPathEntry(travelJob.path().getFirst(), travelJob.path().getLast());
            this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()), dtos -> {
                int speed = this.shipService.getFleetSpeed(dtos);
                List<DistancePoint> distancePoints = this.createCoordinatedPath(entry, travelJob.path().getFirst(), speed);
                for (int i = 0; i < travelJob.progress()-1; i++) distancePoints.removeFirst();
                DistancePoint location = distancePoints.removeFirst();

                gameFleet.setCurrentPoint(distancePoints.removeFirst());
                gameFleet.setLayoutX(location.getX());
                gameFleet.setLayoutY(location.getY());
                this.selectedFleet.setRotate(this.getDirectionalAngle(
                        gameFleet.getCurrentPoint().getPrev(),
                        gameFleet.getCurrentPoint()));
                this.processSpeedChanged(gameFleet);
                this.selectedFleet = null;
            }, error -> System.out.printf("Caught and error while trying to generate travel paths for the fleets" +
                    "with travel jobs in FleetCoordinationService:\n%s", error.getMessage()));
        } else {
            // Otherwise put fleet near the island on which it is located
            var island = this.islandsService.getIslandComponent(fleet.location());
            DistancePoint parkingPoint = this.findParkingPoint(new DistancePoint(island, null));
            gameFleet.setLayoutX(parkingPoint.getX());
            gameFleet.setLayoutY(parkingPoint.getY());
            gameFleet.setStartingPoint();
            gameFleet.collisionCircle.setRadius(Constants.FLEET_COLLISION_RADIUS);
            this.selectedFleet = null;
        }
    }

    private DistancePoint findParkingPoint(DistancePoint islandPoint) {
        double angle = (random.nextInt(360)-90)*Math.PI/180;
        return new DistancePoint(
                islandPoint.getX() + ISLAND_RADIUS_X + (ISLAND_RADIUS_X + FLEET_FROM_ISLAND_DISTANCE)*Math.cos(angle),
                islandPoint.getY() + ISLAND_RADIUS_Y + (ISLAND_RADIUS_X + FLEET_FROM_ISLAND_DISTANCE)*Math.sin(angle),
                POINT_TYPE.ISLAND,
                islandPoint.getPrev()
        );
    }

    public void setInGameController(InGameController inGameController) {
        this.mapGrid = inGameController.mapGrid.getChildren();

        this.timerService.onGameTicked(this::processTravel);
        this.timerService.onSpeedChanged(this::processSpeedChanged);
    }

    public Fleet getSelectedFleet() {
        if (Objects.nonNull(this.selectedFleet)) {
            return this.selectedFleet.getFleet();
        }
        return null;
    }

    private void processSpeedChanged() {
        this.coordinatedPaths.keySet().forEach(this::processSpeedChanged);
    }

    private void processSpeedChanged(GameFleetController fleet) {
        if (this.timerService.getServerSpeed() == 0) fleet.stopTravel();
        else fleet.travelToPoint(this.createSpeedChangedKeyframe(fleet), fleet.getCurrentPoint());
    }

    private void processTravel() {
        this.coordinatedPaths.forEach((fleet, points) -> {
            if (!points.isEmpty())
                fleet.travelToPoint(this.createTravelKeyFrames(fleet, points.getFirst(), 60), points.removeFirst());
        });
    }

    private void processTravel(GameFleetController fleet) {
        fleet.travelToPoint(this.createTravelKeyFrames(fleet, this.coordinatedPaths.get(fleet).getFirst(), 60),
                this.coordinatedPaths.get(fleet).removeFirst());
    }

    private void processFinish(GameFleetController fleet, IslandComponent finishIsland) {
        DistancePoint endPoint = new DistancePoint(finishIsland, fleet.getCurrentPoint());
        fleet.travelToPoint(this.createTravelKeyFrames(fleet, endPoint, 60), endPoint);
    }

    private void processReturn(GameFleetController fleet) {
        DistancePoint prevIslandPoint;
        if (Objects.isNull(fleet.getCurrentPoint().getPrev())) {
            prevIslandPoint = fleet.getCurrentPoint();
        } else {
            prevIslandPoint = fleet.getCurrentPoint().getPrev();
            while (Objects.nonNull(prevIslandPoint.getPrev()) && !prevIslandPoint.getType().equals(POINT_TYPE.ISLAND))
                prevIslandPoint = prevIslandPoint.getPrev();
        }
        fleet.travelToPoint(this.createReturnKeyFrames(fleet, prevIslandPoint), prevIslandPoint);
    }

    private void processTravelForEnemyFleets(Fleet fleet) {
        if (!fleet.empire().equals(this.tokenStorage.getEmpireId())) {
            this.enemyFleets.stream().filter(gameFleet -> gameFleet.getFleet().equals(fleet))
                    .findFirst().map(gameFleet -> {
                        DistancePoint destination = this.findParkingPoint(new DistancePoint(
                                this.islandsService.getIslandComponent(fleet.location()),
                                gameFleet.getCurrentLocation()));
                        gameFleet.travelToPoint(this.createTravelKeyFrames(gameFleet, destination, 24), destination);
                        gameFleet.setFleet(fleet);
                        return gameFleet;
                    }).orElseThrow();
        }
    }

    private double getDirectionalAngle(DistancePoint prevPoint, DistancePoint nextPoint) {
        Point2D delta = nextPoint.subtract(prevPoint);
        return Math.atan2(delta.getY(), delta.getX())*180/Math.PI+90;
    }

    private List<KeyFrame> createTravelKeyFrames(GameFleetController fleet, DistancePoint nextPoint, double speed) {
        return List.of(
                new KeyFrame(Duration.seconds(ROTATE_DURATION),
                        new KeyValue(fleet.rotateProperty(), getDirectionalAngle(nextPoint.getPrev(), nextPoint), Interpolator.EASE_BOTH),
                        new KeyValue(fleet.layoutXProperty(), nextPoint.getPrev().getX()-FLEET_HW, Interpolator.EASE_BOTH),
                        new KeyValue(fleet.layoutYProperty(), nextPoint.getPrev().getY()-FLEET_HW, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds((speed/ (double) this.timerService.getServerSpeed())-ROTATE_DURATION),
                        new KeyValue(fleet.layoutXProperty(), nextPoint.getX()-FLEET_HW, Interpolator.LINEAR),
                        new KeyValue(fleet.layoutYProperty(), nextPoint.getY()-FLEET_HW, Interpolator.LINEAR))
        );

    }

    private List<KeyFrame> createSpeedChangedKeyframe(GameFleetController fleet) {
        DistancePoint nextPoint       = fleet.getCurrentPoint(),
                      currentLocation = fleet.getCurrentLocation();
        double distance         = Math.abs(nextPoint.getPrev().distance(nextPoint)),
               traveledDistance = Math.abs(currentLocation.distance(nextPoint)),
               timeDiff         = 60*(traveledDistance/distance);

        return List.of(
                new KeyFrame(Duration.seconds(timeDiff/this.timerService.getServerSpeed() + ROTATE_DURATION),
                new KeyValue(fleet.layoutXProperty(), nextPoint.getX()-FLEET_HW, Interpolator.LINEAR),
                new KeyValue(fleet.layoutYProperty(), nextPoint.getY()-FLEET_HW, Interpolator.LINEAR))
        );
    }

    private List<KeyFrame> createReturnKeyFrames(GameFleetController fleet, DistancePoint returnPoint) {
        DistancePoint parkingPoint = this.findParkingPoint(returnPoint);
        return List.of(
                new KeyFrame(Duration.seconds(ROTATE_DURATION),
                        new KeyValue(fleet.rotateProperty(), getDirectionalAngle(
                                fleet.getCurrentLocation(), parkingPoint), Interpolator.EASE_BOTH)),

                new KeyFrame(Duration.seconds(4),
                        new KeyValue(fleet.layoutXProperty(), parkingPoint.getX(), Interpolator.LINEAR),
                        new KeyValue(fleet.layoutYProperty(), parkingPoint.getY(), Interpolator.LINEAR)));
    }

    public void travelToIsland(String destinationIslandID) {
        if (Objects.isNull(this.selectedFleet)) return;

        // The path entry should be generated at this point!
        PathEntry entry = this.getPathEntry(this.selectedFleet.getFleet().location(), destinationIslandID);
        this.subscriber.subscribe(this.shipService.getShipsOfFleet(this.selectedFleet.getFleet()._id()), dtos -> {
            int speed = this.shipService.getFleetSpeed(dtos);
            this.createCoordinatedPath(entry, this.selectedFleet.getFleet().location(), speed);

            this.subscriber.subscribe(this.fleetService.beginTravelJob(entry.getPathFromLocation(
                            this.selectedFleet.getFleet().location()), this.selectedFleet.getFleet()._id()), job -> {
                        this.processTravel(this.selectedFleet);
                        this.setOnJobDeletion(job);
                        this.setOnJobCompletion(job);
                    }, error -> System.out.println("Caught an exception while trying to create a new travel job in the" +
                            "FleetCoordinationService:\n" + error.getMessage()));
        }, error -> System.out.printf("Caught an error while trying to get fleet speed in FleetCoordinationService:\n%s",
                error.getMessage()));
    }

    private List<DistancePoint> createCoordinatedPath(PathEntry pathEntry, String startingLocation, int speed) {
        ArrayList<String> path = pathEntry.getPathFromLocation(startingLocation);
        List<DistancePoint> coordinatedPath = new ArrayList<>();
        IslandComponent island = this.islandsService.getIslandComponent(path.getFirst());

        // Get the needed total number of points that need to be put between the islands
        byte interPoints = (byte) (this.getTravelDuration(pathEntry, speed) - (path.size()-1));
        // Get the number of points that should be put between two islands
        byte pointAlloc  = (byte) (interPoints/(path.size()-1));

        coordinatedPath.add(this.findParkingPoint(new DistancePoint(island, null)));

        // Put the intermediate points first, then the island location
        for (int i = 0; i < path.size()-2; i++) {
            // Intermediate points
            this.islandsService.generateDistancePoints(path.get(i), path.get(i+1), pointAlloc)
                    .forEach(point -> coordinatedPath.add(new DistancePoint(point.getX(), point.getY(),
                                      POINT_TYPE.INTER, coordinatedPath.getLast())));

            // Island location
            island = this.islandsService.getIslandComponent(path.get(i+1));
            coordinatedPath.add(this.findParkingPoint(new DistancePoint(island, coordinatedPath.getLast())));
        }

        // Fill the remaining path with points that were not allocated
        this.islandsService.generateDistancePoints(path.get(path.size()-2), path.getLast(), interPoints-pointAlloc*(path.size()-2))
                .forEach(point -> coordinatedPath.add(new DistancePoint(point.getX(), point.getY(),
                                  POINT_TYPE.INTER, coordinatedPath.getLast())));

        island = this.islandsService.getIslandComponent(path.getLast());
        coordinatedPath.add(this.findParkingPoint(new DistancePoint(island, coordinatedPath.getLast())));

        coordinatedPath.removeFirst();
        this.coordinatedPaths.put(this.selectedFleet, coordinatedPath);
        return coordinatedPath;
    }

    public void generateTravelPaths(String startLocation, String endLocation) {
        // Find the shortest path using Dijkstra's algorithm
        if (Objects.isNull(this.selectedFleet)) return;

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
    public int getTravelDuration(String startIslandID, String endIslandID, int speed) {
        return this.getTravelDuration(this.getPathEntry(startIslandID, endIslandID), speed);
    }

    public int getTravelDuration(PathEntry entry, int speed) {
        return (int) Math.ceil((double) entry.getDistance()/speed);
    }

    public void onFleetSelected(Consumer<Fleet> func) {
        this.onFleetSelectedConsumers.add(func);
    }

    public void dispose() {
        this.selectedFleet = null;
        this.subscriber.dispose();
        this.coordinatedPaths.clear();
        this.enemyFleets.clear();
        this.pathEntries.clear();
        this.onFleetSelectedConsumers.clear();
    }
}
