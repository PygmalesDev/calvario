package de.uniks.stp24.component.game;


import de.uniks.stp24.App;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.ws.EventListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "StorageOverview.fxml")
public class StorageOverviewComponent extends VBox {
    @FXML
    Button closeStorageOverviewButton;
    @FXML
    ListView<Resource> resourceListView;
    @FXML
    Label empireNameLabel;


    @Inject
    App app;
    @Inject
    Subscriber subscriber;
    @Inject
    ResourcesService resourcesService;
    @Inject
    EmpireService empireService;
    @Inject
    EventListener eventListener;
    @Inject
    TokenStorage tokenStorage;

    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;



    @Inject
    public StorageOverviewComponent() {

    }


    public void closeStorageOverview(){
        System.out.println("storage game:"
                + tokenStorage.getGameId());
        System.out.println("storage empire:"
                +tokenStorage.getEmpireId());
        this.getParent().setVisible(false);
    }


    @OnDestroy
    void destroy(){
        this.subscriber.dispose();
    }
}
