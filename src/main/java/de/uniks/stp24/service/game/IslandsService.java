package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.ShortSystemDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.UpdateBuildingDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.BasicService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class IslandsService extends BasicService {

    @Inject
    public GameSystemsApiService gameSystemsService;
    @Inject
    LobbyService lobbyService;

    public boolean keyCodeFlag = true;

    String gameID;

    static final int SCALE_FACTOR = 16;
    static final float DISTANCE_FACTOR = 1.2f;
    double minX, maxX, minY, maxY;
    double widthRange, heightRange;
    private final List<ShortSystemDto> devIsles = new ArrayList<>();
    public List<Island> isles = new ArrayList<>();
    private final List<IslandComponent> islandComponentList = new ArrayList<>();
    public final Map<String, IslandComponent> islandComponentMap = new HashMap<>();
    private final Map<String, ReadEmpireDto> empiresInGame = new HashMap<>();
    private final Map<String, Map<String, Integer>> connections = new HashMap<>();
    public final Map<String, InfrastructureService> siteManager = new HashMap<>();

    @Inject
    public IslandsService() {
        if (subscriber == null) subscriber = new Subscriber();
    }

    /**
     * the empires in game are at start known:
     * there are players with empires (and maybe spectators)
     * use an extra manager for handle systems without owner
     * after colonizing (or something else where the owner changes)
     * data in management should be refreshed
     */
    @OnInit
    public void createEmpireServices() {
        empiresInGame.keySet().forEach(id -> siteManager.put(id,new InfrastructureService()));
        siteManager.put("noBody", new InfrastructureService());
    }

    public int getSiteManagerSize() {
        return siteManager.size();
    }

   
    public void setFlag(boolean selected) {
        islandComponentMap.forEach((id, comp) -> {
            if(selected) comp.showFlag(true);
            else if (!comp.islandIsSelected) {
                comp.showFlag(false);
                keyCodeFlag = false;
            }
        });
    }

    /**
     * this method will be used when changing from lobby to ingame
     * and retrieve island information when starting or rejoining a game
     */
    public void retrieveIslands(String gameID) {
        resetVariables();
        createEmpireServices();
        this.gameID = gameID;
        subscriber.subscribe(gameSystemsService.getSystems(gameID),
                dto -> {
                    Arrays.stream(dto).forEach(data -> {
                        connections.put(data._id(), data.links());
                        minX = Math.min(data.x(),minX);
                        minY = Math.min(data.y(),minY);
                        maxX = Math.max(data.x(),maxX);
                        maxY = Math.max(data.y(),maxY);
                        Island tmp = convertToIsland(data);
                        isles.add(tmp);
                    });
                    widthRange = maxX-minX + 1000;
                    heightRange = maxY-minY + 1000;
                    this.app.show("/ingame");
                    refreshListOfColonizedSystems();
                },
                error -> System.out.println("Error while retrieving islands in the IslandsService:\n"+error.getMessage()));
    }

    /**
     * coordinate system on server has origin near to screen center
     * and their range varies depending on game settings (size).
     * an offset to match the screen size will be calculated depending on
     * width and height thus the size of the pane should be considered.
     * IMPORTANT:
     * due the dropshadow-effect the component size will grow!
     * this means that if effect's radius (now 2.0) is large
     * an island component can be clicked just by clicking near (or far) from it
     */
    public IslandComponent createIslandPaneFromDto(Island isleDto, @NotNull IslandComponent component) {
        component.applyInfo(isleDto);
        double screenOffsetH = widthRange * (SCALE_FACTOR + 2) / 2.0 - 25;
        double screenOffSetV = heightRange * (SCALE_FACTOR + 2) / 2.0 - 25;
        double serverOffsetH = minX + 0.5 * widthRange;
        double serverOffsetV = minY + 0.5 * heightRange;
        component.setPosition(SCALE_FACTOR * isleDto.posX()*DISTANCE_FACTOR - serverOffsetH + screenOffsetH,
                SCALE_FACTOR * isleDto.posY()*DISTANCE_FACTOR - serverOffsetV + screenOffSetV);
        component.applyIcon(isleDto.type());
        component.setFlagImage(isleDto.flagIndex());
        applyDropShadowToIsland(component);
        return component;
    }

    // return mapRange * (SCALE_FACTOR + 3)
    public double getMapWidth() {
        return this.widthRange * (SCALE_FACTOR + 3);
    }

    public double getMapHeight() {
        return this.heightRange * (SCALE_FACTOR + 3);
    }

    public Map<String, List<String>> getConnections() {
        Map<String, List<String>> singleConnections = new HashMap<>();
        List<String> checked = new ArrayList<>();
        connections.forEach((key, value) -> {
            if (!checked.contains(key)) checked.add(key);
            ArrayList<String> tmp = new ArrayList<>();
            for (String s : value.keySet())
                if (!checked.contains(s)) tmp.add(s);
            singleConnections.putIfAbsent(key, tmp);
        });
        return singleConnections;
    }

    public Map<String, Integer> getConnections(String islandID) {
        return this.connections.get(islandID);
    }

    /**
     * create subcomponents to be added to the map
     * put information in a map to access them easily
     */
    public List<IslandComponent> createIslands(@NotNull List<Island> list) {
        list.forEach(
          island -> {
              IslandComponent tmp = createIslandPaneFromDto(island,
                app.initAndRender(new IslandComponent()));
              tmp.setLayoutX(tmp.getPosX());
              tmp.setLayoutY(tmp.getPosY());
              tmp.setIslandService(this);
              islandComponentList.add(tmp);
              islandComponentMap.put(island.id(), tmp);
          }
        );
        return Collections.unmodifiableList(islandComponentList);
    }

    /**
     * lines (as object) between islands
     */
    public List<Line> createLines(Map<String, IslandComponent> idToComponent) {
        Map<String, List<String>> islandConnections = getConnections();
        List<Line> linesInMap = new ArrayList<>();
        islandConnections.forEach((isle, list) -> {
            double startX, startY, endX, endY;
            IslandComponent isle1 = idToComponent.get(isle);
            startX = isle1.getPosX() + 60;
            startY = isle1.getPosY() + 100;
            for (String neighbour : list) {
                IslandComponent isle2 = idToComponent.get(neighbour);
                endX = isle2.getPosX() + 60;
                endY = isle2.getPosY() + 100;

                Line tmp = new Line(startX, startY, endX, endY);
                tmp.getStyleClass().add("connection");
                linesInMap.add(tmp);
            }
        });
        return linesInMap;
    }

    private void resetVariables() {
        minX = 0.0;
        minY = 0.0;
        maxX = 0.0;
        maxY = 0.0;
        widthRange = 0.0;
        heightRange = 0.0;
    }

    /** method should retrieve frequently updated information
     */
    public void refreshListOfColonizedSystems() {
        devIsles.clear();
        subscriber.subscribe(gameSystemsService.getSystems(this.gameID), dto -> {
              Arrays.stream(dto).forEach(data -> {
                    if(Objects.nonNull(data.owner())) {
                        ShortSystemDto tmp = new ShortSystemDto(data.owner(),
                                data._id(),
                                data.type(),
                                Objects.isNull(data.name()) ? "Uncharted Island" : data.name(),
                                data.districtSlots(),
                                data.districts(),
                                data.capacity(),
                                data.buildings(),
                                data.upgrade(),
                                data.population()
                        );
                        devIsles.add(tmp);
                    }
              });
              mapSitesBuildings();
          },
          error -> System.out.printf(
                  "Caught an error while refreshing colonized systems list in Islands Service:\n %s", error.getMessage()));
    }

    /** after refresh list remap site and buildings (?)
     * building's information is in an array
     * */
    public void mapSitesBuildings() {
        siteManager.forEach((id,manager) -> manager.resetMap());
        for (ShortSystemDto dto : this.devIsles) {
            if (Objects.nonNull(dto.districts())) {
                dto.districts().forEach((k, v) -> siteManager.get(dto.owner()).putOrUpdateSiteInfo(k, v));
            }
            if (Objects.nonNull(dto.buildings())){
                dto.buildings()
                  .forEach(building -> siteManager.get(dto.owner()).putOrUpdateBuildingInfo(building));
            }
        }
    }

    public int getCapacityOfOneSystem(String id) {
        for (ShortSystemDto dto : devIsles) {
            if (dto._id().equals(id)) {
                return siteManager.get(dto.owner()).getTotalCapacity(dto.districts());
            }
        }
        return 0;
    }

    public int getAllNumberOfSites(String empireID) {
        return siteManager.get(empireID).getTotalSiteCapacity();
    }

    public int getNumberOfSites(String empireID, String siteID) {
        int total = 0;
        if (empiresInGame.containsKey(empireID)) {
            total = siteManager.get(empireID).getSiteCapacities(siteID);
        }
        return total;
    }

    public List<Island> getListOfIslands() {
        return Collections.unmodifiableList(this.isles);
    }

    public Map<String, IslandComponent> getComponentMap() {
        return Collections.unmodifiableMap(this.islandComponentMap);
    }

    public ReadEmpireDto getEmpire(String id) {
        return this.empiresInGame.getOrDefault(id, null);
    }

    public void saveEmpire(String id, ReadEmpireDto empire) {
        this.empiresInGame.put(id, empire);
    }

    /**
     * after color was modified using .brighter() compute it to a string
     */
    private @NotNull String colorToRGB(Color color) {
        return "rgb(" + (int) (color.getRed() * 255) + "," +
                (int) (color.getGreen() * 255) + "," +
                (int) (color.getBlue() * 255) + ")";
    }

    public void removeDataForMap() {
        this.devIsles.clear();
        this.isles.clear();
        this.islandComponentList.forEach(IslandComponent::destroy);
        this.islandComponentList.clear();
        this.islandComponentMap.clear();
        this.empiresInGame.clear();
        this.connections.clear();
        this.siteManager.clear();
    }

    public void updateIslandBuildings(IslandAttributeStorage islandAttributes, InGameController inGameController, ArrayList<String> buildings){
        if (Objects.nonNull(inGameController.selectedIsland) && Objects.nonNull(islandAttributes.getIsland())) {
            this.subscriber.subscribe(gameSystemsService.updateBuildings(
                    tokenStorage.getGameId(), islandAttributes.getIsland().id(),
                    new UpdateBuildingDto(buildings)), result -> {

                islandAttributes.getIsland().buildings().clear();
                islandAttributes.getIsland().buildings().addAll(result.buildings());

                inGameController.selectedIsland.island = islandAttributes.getIsland();
            });
        }
        inGameController.overviewSitesComponent.updateResCapacity();
    }

    public Island getIsland(String islandID) {
        return this.isles.stream()
                .filter(isle -> isle.id().equals(islandID))
                .findFirst().orElse(null);
    }

    public IslandComponent getIslandComponent(String islandID) {
        return this.islandComponentList.stream().filter(islandComponent ->
                islandComponent.island.id().equals(islandID))
                .findFirst().orElse(null);
    }

    public String getIslandName(String islandID) {
        Island island = this.isles.stream().filter(isle -> isle.id().equals(islandID)).findFirst().orElse(null);
        if (Objects.nonNull(island) && Objects.nonNull(island.name()))
            if (island.name().isEmpty())
                return "Uncharted Island";
            else return island.name();
        return "MissingNo.";
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }

    public Island updateIsland(SystemDto result) {
        Island newIsland = convertToIsland(result);
        this.isles.replaceAll(island -> island.id().equals(newIsland.id()) ? newIsland : island);
        return newIsland;
    }

    public Island convertToIsland(SystemDto result) {
        return new Island(result.owner(),
                Objects.isNull(result.owner()) ? -1 : getEmpire(result.owner()).flag(),
                result.x(),
                result.y(),
                IslandType.valueOf(result.type()),
                result.population(),
                result.capacity(),
                result.upgrade().ordinal(),
                result.districtSlots(),
                result.districts(),
                result.buildings(),
                result._id(),
                result.upgrade().toString(),
                Objects.isNull(result.name()) ? "Uncharted Island" : result.name()
        );
    }

    public void applyDropShadowToIsland(IslandComponent islandComponent) {
        // adds a background color to island as same as owner empire color
        if (Objects.nonNull(islandComponent.island.owner())) {
            Color colorWeb = Color.web(getEmpire(islandComponent.island.owner()).color()).brighter();
            islandComponent.islandImage.setStyle("-fx-effect: dropshadow(gaussian," + colorToRGB(colorWeb) + ", 4.0, 0.88, 0, 0);");}

    }
}
