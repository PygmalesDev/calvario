package de.uniks.stp24.service;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.rest.GameSystemsService;
import javafx.geometry.Point2D;
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
    GameSystemsService gameSystemsService;
    @Inject
    ErrorService errorService;

    private final List<Island> isles = new ArrayList<>();
    // private final Map<String, List<String>>  = new HashMap<>();
    static final RandomGenerator randomGenerator = new Random(1234);

    @Inject
    public IslandsService() {}


    // this method will be used by changing from lobby to ingame
    //
    public void retrieveIslands(String gameID) {
        this.isles.clear();
        subscriber.subscribe(gameSystemsService.getSystems(gameID),
            dto -> {
                Arrays.stream(dto).forEach(data -> {
                    // List<String> = new ArrayList<>(data.links().keySet());
                    System.out.println(data.links().keySet().size() + " " + data.type()
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
                System.out.println(isles.size() + " islands generated");
                this.app.show("/ingame", Map.of("gameID", gameID));
            },
          error -> errorService.getStatus(error));
    }


    public List<Island> getListOfIslands() {
        return Collections.unmodifiableList(this.isles);
    }

    /*
    at the moment island list is not available when the map is rendering.
    this is because  of the asynchronous response :(

   therefore there are some methods that would be used
   and some that could be removed
    */

    // coordinate system on server has origin at screen center
    // and are not too big apply a factor 10 for increase and
    // an offset to match our screen size
    public IslandComponent createIslandPaneFromDto(Island isleDto, IslandComponent component) {
        component.applyInfo(isleDto);
        double offsetV = 600.0;
        double offsetH = 900.0;
        component.setPosition(isleDto.posX() * 6 + offsetH,
          isleDto.posY() * 6 + offsetV);
        // todo read values from dto
        component.applyIcon(isleDto.type());
        int flag = randomGenerator.nextInt(0, 5);
//        component.applyIcon(IslandType.values()[icon]);

        component.setFlagImage(flag);

        return component;
    }

    // for tests
    public List<Point2D> testRender(){
        double x, y;
        List<Point2D> test = new ArrayList<>();
        while (test.size() < 70) {
            x = randomGenerator.nextDouble(1800);
            y = randomGenerator.nextDouble(1200);
            Point2D tmp = new Point2D(x,y);
            if (!test.contains(tmp)) test.add(tmp);
        }
        return test;
    }

    // for tests or offline (?)
    // set position and icon for the map
    public IslandComponent createIslandPane(Point2D p, IslandComponent isle) {
        isle.setPosition(p.getX(),p.getY());
        int icon = randomGenerator.nextInt(0, 6);
        int flag = randomGenerator.nextInt(0, 5);
        isle.applyIcon(IslandType.values()[icon]);
        isle.setFlagImage(flag);
        return isle;
    }

    @OnDestroy
    public void destroy(){
        this.subscriber.dispose();
    }
}
