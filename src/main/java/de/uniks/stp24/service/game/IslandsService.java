package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.BasicService;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class IslandsService extends BasicService {

    @Inject
    public GameSystemsApiService gameSystemsService;
    @Inject
    LobbyService lobbyService;

    static final int factor = 10;
    double minX,maxX,minY,maxY;
    double widthRange, heightRange;
    public List<Island> isles = new ArrayList<>();
    private final List<IslandComponent> islandComponentList = new ArrayList<>();
    private final Map<String, IslandComponent> islandComponentMap = new HashMap<>();
    private final Map<String, ReadEmpireDto> empiresInGame = new HashMap<>();
    private final Map<String, List<String>> connections = new HashMap<>();

    @Inject
    public IslandsService() {
        if (subscriber==null) subscriber = new Subscriber();
    }

    /** this method will be used when changing from lobby to ingame
     * and retrieve island information when starting or rejoining a game
     */
    public void retrieveIslands(String gameID) {
        resetVariables();
        subscriber.subscribe(gameSystemsService.getSystems(gameID),
                dto -> {
                    Arrays.stream(dto).forEach(data -> {
                        List<String> linkedIsles = new ArrayList<>(data.links().keySet());
                        minX = Math.min(data.x(),minX);
                        minY = Math.min(data.y(),minY);
                        maxX = Math.max(data.x(),maxX);
                        maxY = Math.max(data.y(),maxY);
                        Island tmp = new Island(data.owner(),
                                Objects.isNull(data.owner()) ? -1 : getEmpire(data.owner()).flag(),
                                data.x(),
                                data.y(),
                                IslandType.valueOf(data.type()),
                                data.population(),
                                data.capacity(),
                                data.upgrade().ordinal(),
                                data.districtSlots(),
                                data.districts(),
                                data.buildings(),
                                data._id()
                        );
                        isles.add(tmp);
                        connections.put(data._id(),linkedIsles);
                    });
                    widthRange = maxX-minX;
                    heightRange = maxY-minY;
                    this.app.show("/ingame");
                },
                error -> errorService.getStatus(error));
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
    public IslandComponent createIslandPaneFromDto(Island isleDto, IslandComponent component) {
        component.applyInfo(isleDto);
        double screenOffsetH = widthRange * (factor + 2) / 2.0 - 25;
        double screenOffSetV = heightRange * (factor + 2) / 2.0 - 25;
        double serverOffsetH = minX + 0.5 * widthRange;
        double serverOffsetV = minY + 0.5 * heightRange;
        component.setPosition(factor * isleDto.posX() - serverOffsetH + screenOffsetH,
                factor * isleDto.posY() - serverOffsetV + screenOffSetV);
        component.applyIcon(isleDto.type());
        component.setFlagImage(isleDto.flagIndex());
        if(Objects.nonNull(isleDto.owner())) {
            Color colorWeb = Color.web(getEmpire(isleDto.owner()).color()).brighter();
            component.setStyle("-fx-effect: dropshadow(gaussian," + colorToRGB(colorWeb)+ ", 2.0, 0.88, 0, 0);");
        }
        return component;
    }

    // return mapRange * (factor + 3)
    public double getMapWidth() {
        return this.widthRange * (factor + 3);
    }
    public double getMapHeight() {
        return this.heightRange * (factor + 3);
    }
    public Map<String, List<String>> getConnections() {
        Map<String, List<String>> singleConnections = new HashMap<>();
        List<String> checked = new ArrayList<>();
        connections.forEach((key,value) -> {
            if (!checked.contains(key)) checked.add(key);
            ArrayList<String> tmp = new ArrayList<>();
            for (String s : value) {
                if (!checked.contains(s)) {
                    tmp.add(s);
                }
            }
            singleConnections.putIfAbsent(key,tmp);
        });
        return singleConnections;
    }

    /**
     * create subcomponents to be added to the map
     * put information in a map to access them easily
     */
    public List<IslandComponent> createIslands(List<Island> list) {
        list.forEach(
                island -> {
//              IslandComponent tmp1 = new IslandComponent();
                    IslandComponent tmp = createIslandPaneFromDto(island,
                            app.initAndRender(new IslandComponent())); // isn't working anymore?!
//                tmp1);

                    tmp.setLayoutX(tmp.getPosX());
                    tmp.setLayoutY(tmp.getPosY());
                    islandComponentList.add(tmp);
                    islandComponentMap.put(island.id(), tmp);
                }
        );
        return Collections.unmodifiableList(islandComponentList);
    }

    /** lines (as object) between islands */
    public List<Line> createLines(Map<String,IslandComponent> idToComponent) {
        Map<String, List<String>> islandConnections = getConnections();
        List<Line> linesInMap = new ArrayList<>();
        islandConnections.forEach((isle,list) -> {
            double startX, startY, endX, endY;
            IslandComponent isle1 = idToComponent.get(isle);
            startX = isle1.getPosX() + 25;
            startY = isle1.getPosY() + 25;
            for (String neighbour : list) {
                IslandComponent isle2 = idToComponent.get(neighbour);
                endX = isle2.getPosX() + 25;
                endY = isle2.getPosY() + 25;
                Line tmp = new Line(startX,startY,endX,endY);
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

    public List<Island> getListOfIslands() { System.out.println("list of isles " + isles.size());
        return Collections.unmodifiableList(this.isles);
    }

    public Map<String, IslandComponent> getComponentMap() {
        return Collections.unmodifiableMap(this.islandComponentMap);
    }

    public ReadEmpireDto getEmpire(String id){
        return this.empiresInGame.getOrDefault(id,null);
    }

    public void saveEmpire(String id, ReadEmpireDto empire){
        this.empiresInGame.put(id,empire);
    }

    /** after color was modified using .brighter() compute it to a string */
    private String colorToRGB(Color color) {
        return "rgb(" + (int) (color.getRed() * 255) + "," +
                (int) (color.getGreen() * 255) + "," +
                (int) (color.getBlue() * 255) + ")" ;
    }

    public void removeDataForMap() {
        this.isles.clear();
        this.islandComponentList.forEach(IslandComponent::destroy);
        this.islandComponentList.clear();
        this.islandComponentMap.clear();
        this.empiresInGame.clear();
        this.connections.clear();
    }

    @OnDestroy
    public void destroy(){
        this.subscriber.dispose();
    }

}
