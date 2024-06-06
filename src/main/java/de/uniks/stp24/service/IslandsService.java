package de.uniks.stp24.service;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.rest.GameSystemsApiService;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.random.RandomGenerator;
@Singleton
public class IslandsService {
    @Inject
    App app;
    @Inject
    Subscriber subscriber;
    @Inject
    GameSystemsApiService gameSystemsService;
    @Inject
    ErrorService errorService;
    @Inject
    TokenStorage tokenStorage;

    private final List<Island> isles = new ArrayList<>();
    // private final Map<String, List<String>>  = new HashMap<>();

    // after development is ready remove this
    static final RandomGenerator randomGenerator = new Random(1234);

    @Inject
    public IslandsService() {}

    // this method will be used when changing from lobby to ingame
    // and retrieve islands when game starts
    public void retrieveIslands(String gameID) {
        this.isles.clear();
        subscriber.subscribe(gameSystemsService.getSystems(gameID),
            dto -> {
                Arrays.stream(dto).forEach(data -> {
                    List<String> linkedIsles = new ArrayList<>(data.links().keySet());
                    System.out.println(linkedIsles.size() + " " + data.type()
                    + " " + data.x() + " " + data.y() + " " + data.owner() );
                    Island tmp = new Island(data.owner(),
                        1,
                        data.x(),
                        data.y(),
                        IslandType.valueOf(data.type()),
                        data.population(),
                        data.capacity(),
                        data.upgrade().ordinal());
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
     * and are not too big apply a factor 10 for increase and
     * an offset to match our screen size
     *  thus the size of the pane should be considered
     */
    public IslandComponent createIslandPaneFromDto(Island isleDto, IslandComponent component) {
        component.applyInfo(isleDto);
        double offsetH = 900.0 - component.widthProperty().getValue() * 0.5;
        double offsetV = 600.0 - component.heightProperty().getValue() * 0.5;
        component.setPosition(isleDto.posX() * 6 + offsetH,
          isleDto.posY() * 6 + offsetV);
        // todo read values from dto
        component.applyIcon(isleDto.type());
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
