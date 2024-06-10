package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.SystemsDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.BasicService;
import de.uniks.stp24.service.menu.LobbyService;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import org.fulib.fx.annotation.event.OnDestroy;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.random.RandomGenerator;

import static java.lang.Thread.sleep;

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

    @Inject
    public IslandsService() {}

    // this method will be used when changing from lobby to ingame
    // and retrieve islands when game starts
    // todo remove printouts
    public void retrieveIslands(String gameID) {
        this.isles.clear();
        subscriber.subscribe(gameSystemsService.getSystems(gameID),
            dto -> {
                Arrays.stream(dto).forEach(data -> {
                    List<String> linkedIsles = new ArrayList<>(data.links().keySet());
                    System.out.println(data);
                    Island tmp = new Island(data.owner(),
                        data.upgrade(),
                        data.name(),
                        data._id(),
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
                });
                this.app.show("/ingame");
            },
            error -> errorService.getStatus(error));
    }

    public List<Island> getListOfIslands() {
        return Collections.unmodifiableList(this.isles);
    }

    /**
     * coordinate system on server has origin at screen center
     * and their range is approx. (-100,100) ?
     * apply a factor 6  for increase and
     * an offset to match our screen size
     * it is set to 2560 x 1440
     * thus the size of the pane should be considered
     */
    //todo set screen resolution, factor and offset depending on game size
    public IslandComponent createIslandPaneFromDto(Island isleDto, IslandComponent component) {
        component.applyInfo(isleDto);
        double offsetH = 1280 - component.widthProperty().getValue() * 0.5;
        double offsetV = 720 - component.heightProperty().getValue() * 0.5;
        component.setPosition(isleDto.posX() * 6 + offsetH,
          isleDto.posY() * 6 + offsetV);
        component.applyIcon(isleDto.type());
        component.setFlagImage(isleDto.flagIndex());
        return component;
    }

    @OnDestroy
    public void destroy(){
        this.subscriber.dispose();
    }

    public Island updateIsland(SystemDto result) {
        return new Island(result.owner(),
                result.upgrade(),
                result.name(),
                result._id(),
                Objects.isNull(result.owner()) ? -1 : tokenStorage.getFlagIndex(result.owner()),
                result.x(),
                result.y(),
                IslandType.valueOf(result.type()),
                result.population(),
                result.capacity(),
                result.upgrade().ordinal(),
                // todo find out which information in data match sites in island dto
                result.districtSlots(),
                result.districts(),
                result.buildings()
        );
    }
}
