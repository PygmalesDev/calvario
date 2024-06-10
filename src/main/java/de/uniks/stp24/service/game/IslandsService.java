package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.BasicService;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import org.fulib.fx.annotation.event.OnDestroy;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.random.RandomGenerator;
@Singleton
public class IslandsService extends BasicService {

    @Inject
    GameSystemsApiService gameSystemsService;
    @Inject
    LobbyService lobbyService;

    private final List<Island> isles = new ArrayList<>();
    // todo use this map for connections
    // private final Map<String, List<String>>  = new HashMap<>();
    // todo after development is ready remove this generator
    static final RandomGenerator randomGenerator = new Random(1234);
    static final DropShadow drop = new DropShadow();
    double minX,maxX,minY,maxY;
    double widthRange, heightRange;

    @Inject
    public IslandsService() {
        drop.setColor(Color.LIGHTYELLOW.saturate());
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
                    System.out.println(data);
                    minX = Math.min(data.x(),minX);
                    minY = Math.min(data.y(),minY);
                    maxX = Math.max(data.x(),maxX);
                    maxY = Math.max(data.y(),maxY);
                    Island tmp = new Island(data.owner(),
                        Objects.isNull(data.owner()) ? -1 : tokenStorage.getFlagIndex(data.owner()),
                        data.x(),
                        data.y(),
                        IslandType.valueOf(data.type()),
                        data.population(),
                        data.capacity(),
                        data.upgrade().ordinal(),
                        // todo find out which information in data match sites in island dto
                        data.districtSlots(),
                        data.districts(),
                        data.buildings()
                      );
                    isles.add(tmp);
                    widthRange = maxX-minX;
                    heightRange = maxY-minY;
                });
                System.out.print("x:" + minX + "," + maxX);
                System.out.println(" y:" + minY + "," + maxY );
                System.out.println(widthRange + "x" + heightRange);
                this.app.show("/ingame");
            },
            error -> errorService.getStatus(error));
    }

    public List<Island> getListOfIslands() {
        return Collections.unmodifiableList(this.isles);
    }

    /**
     * coordinate system on server has origin at screen center
     * and their range varies depending on game settings (size)
     * game screen should be at least 1920
     *
     * an offset to match the screen size that
     * will be calculated depending on mapRange
     * thus the size of the pane should be considered
     * I think
     */
    //todo set screen resolution, factor and offset depending on game size
    public IslandComponent createIslandPaneFromDto(Island isleDto, IslandComponent component) {
        component.applyInfo(isleDto);
        double screenOffsetH = widthRange * 5.5 - component.widthProperty().getValue() * 0.5;
        double screenOffSetV = heightRange * 5.5 - component.widthProperty().getValue() * 0.5;
        double serverOffsetH = minX + 0.5 * widthRange;
        double serverOffsetV = minY + 0.5 * heightRange;
        component.setPosition(9 * isleDto.posX() - serverOffsetH + screenOffsetH,
          9 * isleDto.posY() - serverOffsetV + screenOffSetV);
        component.applyIcon(isleDto.type());
        component.setFlagImage(isleDto.flagIndex());
        if(Objects.nonNull(isleDto.owner()) && isleDto.owner().equals(tokenStorage.getEmpireId())) {
            component.setEffect(drop);
        }
        return component;
    }

    // return mapRange * (factor + 1)
    public double getMapWidth() {
        //if (this.widthRange * 6 <= 1440) this.widthRange = 1440 / 6.0;
        return this.widthRange * 11;
    }
    public double getMapHeight() {
//        if (this.heightRange * 6 <= 1440) this.heightRange = 1440 / 6.0;
        return this.heightRange * 11;
    }

    private void resetMapRange(){
        minX = 0.0;
        minY = 0.0;
        maxX = 0.0;
        maxY = 0.0;
        widthRange = 0.0;
        heightRange = 0.0;
    }

    @OnDestroy
    public void destroy(){
        this.subscriber.dispose();
    }
}
