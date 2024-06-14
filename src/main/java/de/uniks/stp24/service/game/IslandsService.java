package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.BasicService;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

import static javafx.scene.effect.BlurType.GAUSSIAN;

@Singleton
public class IslandsService extends BasicService {

    @Inject
    public GameSystemsApiService gameSystemsService;
    @Inject
    LobbyService lobbyService;

    private final List<Island> isles = new ArrayList<>();
    static private final Map<String, List<String>> connections = new HashMap<>();
    static final DropShadow drop = new DropShadow();
    static final int factor = 10;
    double minX,maxX,minY,maxY;
    double widthRange, heightRange;
    private final List<IslandComponent> islandComponentList = new ArrayList<>();
    private final Map<String, IslandComponent> islandComponentMap = new HashMap<>();
    private final Map<String, ReadEmpireDto> empiresInGame = new HashMap<>();

    @Inject
    public IslandsService() {
        drop.setColor((Color.CHARTREUSE).brighter());
        drop.setBlurType(GAUSSIAN);
        drop.setRadius(15);
        if (subscriber==null) subscriber = new Subscriber();
    }

    // this method will be used when changing from lobby to ingame
    // and retrieve islands when game starts
    // todo remove printouts
    public void retrieveIslands(String gameID) {
        this.isles.clear();
        resetMapRange();
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
                        // todo find out which information in data match sites in island dto
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
                this.app.show("/ingame"); // for test failing this.app == null
            },
            error -> errorService.getStatus(error));
    }

    /**
     * coordinate system on server has origin at screen center
     * and their range varies depending on game settings (size).
     * an offset to match the screen size will be calculated depending on
     * width and height
     * thus the size of the pane should be considered
     */
    //todo set screen resolution, factor and offset depending on game size
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
        if(Objects.nonNull(isleDto.owner()) && isleDto.owner().equals(tokenStorage.getEmpireId())) {
            component.setEffect(drop);
        }
        return component;
    }

    // return mapRange * (factor + 2)
    public double getMapWidth() {
        return this.widthRange * (factor + 3);
    }
    public double getMapHeight() {
        return this.heightRange * (factor + 3);
    }
    public Map<String, List<String>> getConnections(){
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

    public List<IslandComponent> createIslands(List<Island> list){
        list.forEach(
          island -> {
              IslandComponent tmp = createIslandPaneFromDto(island,
                app.initAndRender(new IslandComponent()));
              tmp.setLayoutX(tmp.getPosX());
              tmp.setLayoutY(tmp.getPosY());
              islandComponentList.add(tmp);
              islandComponentMap.put(island.id(), tmp);
          }
        );
        return Collections.unmodifiableList(islandComponentList);
    }

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
                //todo with css? maybe this #FF7F50
                tmp.styleProperty().set(" -fx-stroke: #FFFFFF; -fx-stroke-dash-array: 5 5; -fx-stroke-width: 2;");
//                tmp.setStyle();
                linesInMap.add(tmp);
            }
        });
        return linesInMap;
    }

    private void resetMapRange(){
        minX = 0.0;
        minY = 0.0;
        maxX = 0.0;
        maxY = 0.0;
        widthRange = 0.0;
        heightRange = 0.0;
    }

    public List<Island> getListOfIslands() {
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

    @OnDestroy
    public void destroy(){
        this.subscriber.dispose();
    }
}
