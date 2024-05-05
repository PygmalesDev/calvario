package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.component.UserComponent;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.TokenStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;
import javax.inject.Inject;
import javax.inject.Provider;

@Title("Enter Game")
@Controller
public class EnterGameController {
    @Inject
    App app;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    UserApiService userApiService;

    @Inject
    Subscriber subscriber;

    @Inject
    Provider<UserComponent> userComponentProvider;

    @FXML
    ListView<User> playerListView;

    private final ObservableList<User> users = FXCollections.observableArrayList();

    @Inject
    public EnterGameController() {

    }

    // TODO: DELETE THIS I'M BEGGING YOU
    @OnInit
    void setToken() {
        tokenStorage.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2NjJiODNkZWE1MWE3ODhiMjNhNzRkNGIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJzdHJpbmciLCJpYXQiOjE3MTQ5MjQxMDcsImV4cCI6MTcxNDkyNzcwN30.8EPvPw-SUwFa5qUXhJYZxsRomD0PC_BGD6OBvXHs2Og");
    }

    @OnInit
    void init() {
        this.subscriber.subscribe(this.userApiService.findAll(), this.users::setAll);
    }

    @OnRender
    void render() {
        this.playerListView.setItems(this.users);
        System.out.println(playerListView);
        this.playerListView.setCellFactory(list ->
                new ComponentListCell<>(this.app, this.userComponentProvider));
    }

    @OnDestroy
    void destroy() {
        subscriber.dispose();
    }

    public void cancel() {
        System.out.println("Canceled");
    }

    public void joinGame() {
        System.out.println("Joined");
    }

}
