package de.uniks.stp24.service;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.rest.GameSystemsService;
import io.reactivex.rxjava3.core.Observable;
import javafx.geometry.Point2D;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

@Singleton
public class IslandsService {
    @Inject
    GameSystemsService gameSystemsService;

    private final List<Island> isles = new ArrayList<>();
    static final RandomGenerator randomGenerator = new Random(1234);

    @Inject
    public IslandsService() {}

    public Observable<Island[]> getIslands(String gameID) {
        return this.gameSystemsService.getSystems(gameID);//
    }

    private void generateIslands(Island[] arrayOfIsland) {
        for (Island isle : arrayOfIsland) {
            Island tmp = new Island(isle.owner(),
              isle.flagIndex(),
              isle.posX(),
              isle.posY(),
              isle.type(),
              isle.crewCapacity(),
              isle.resourceCapacity(),
              isle.upgradeLevel(),
              isle.sites()
            );
            isles.add(tmp);
        }
    }

    public List<Island> getIslands() {
        return Collections.unmodifiableList(this.isles);
    }

    public List<Point2D> testRender(){
        double x, y;
        List<Point2D> test = new ArrayList<>();
        while (test.size() < 70) {
            x = RandomGenerator.getDefault().nextDouble(1800);
            y = RandomGenerator.getDefault().nextDouble(1200);
            Point2D tmp = new Point2D(x,y);
            if (!test.contains(tmp)) test.add(tmp);
        }
        return test;
    }

    public IslandComponent createIslandPane(Point2D p, IslandComponent isle) {
        isle.setPosition(p.getX(),p.getY());
        int icon = RandomGenerator.getDefault().nextInt(0, 6);
        isle.applyIcon(IslandType.values()[icon]);

        return isle;
    }
}
