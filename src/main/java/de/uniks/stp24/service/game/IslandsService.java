package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.BasicService;
import de.uniks.stp24.service.menu.LobbyService;
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
    // private final Map<String, List<String>>  = new HashMap<>();

    // todo after development is ready remove this generator
    static final RandomGenerator randomGenerator = new Random(1234);

    @Inject
    public IslandsService() {}

    // this method will be used when changing from lobby to ingame
    // and retrieve islands when game starts
    public void retrieveIslands(String gameID) {
        this.isles.clear();
        retrieveMembersInfo(gameID);
        subscriber.subscribe(gameSystemsService.getSystems(gameID),
            dto -> {
                Arrays.stream(dto).forEach(data -> {
                    List<String> linkedIsles = new ArrayList<>(data.links().keySet());
                    System.out.println(linkedIsles.size() + " " + data.type()
                    + " " + data.x() + " " + data.y() + " " + data.owner() );
                    Island tmp = new Island(data.owner(),
                        // todo flagIndex could be retrieved from server -> games/{game}/members/{user}
                        1,
                        data.x(),
                        data.y(),
                        IslandType.valueOf(data.type()),
                        data.population(),
                        data.capacity(),
                        data.upgrade().ordinal(),
                        // todo not sure which information in data match sites in island dto
                        null
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

    // look for flagIndex of all players with an empire
    public void retrieveMembersInfo(String gameID){
        subscriber.subscribe(lobbyService.loadPlayers(gameID),
          dto -> {
            Arrays.stream(dto).forEach(member -> {
                if(member.ready() && Objects.nonNull(member.empire())) {
                    tokenStorage.saveFlag(member.user(),member.empire().flag());
                    //todo look if this works properly
                    System.out.println(member.user());
                    System.out.println(member.empire().flag());
                }
            });
          },
          error -> errorService.getStatus(error));
    }

    /**
     * coordinate system on server has origin at screen center
     * and are not too big apply a factor 10 for increase and
     * an offset to match our screen size
     * it is set to 2560 x 1440
     * thus the size of the pane should be considered
     */
    public IslandComponent createIslandPaneFromDto(Island isleDto, IslandComponent component) {
        component.applyInfo(isleDto);
        double offsetH = 1280 - component.widthProperty().getValue() * 0.5;
        double offsetV = 720 - component.heightProperty().getValue() * 0.5;
        component.setPosition(isleDto.posX() * 6 + offsetH,
          isleDto.posY() * 6 + offsetV);
        // todo read values from dto
        component.applyIcon(isleDto.type());
        // todo read values from tokenStorage -> owner = null -> no flag!
        int flag = randomGenerator.nextInt(0, 5);
//        component.applyIcon(IslandType.values()[icon]);

        component.setFlagImage(flag);

        return component;
    }

    @OnDestroy
    public void destroy(){
        this.subscriber.dispose();
    }
}
