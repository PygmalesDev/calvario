package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ContactsService;
import de.uniks.stp24.service.game.WarService;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Provider;

@Component(view = "ContactsComponent.fxml")
public class ContactsComponent extends StackPane {
    @FXML
    ListView<Contact> contactsListView;
    @Inject
    App app;
    @Inject
    ContactsService contactsService;
    @Inject
    WarService warService;
    @Inject
    Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Provider<ContactCell> contactCellProvider;

    public ObservableList<Contact> contactCells = FXCollections.observableArrayList();
    public ObservableList<WarDto> wars = FXCollections.observableArrayList();

    private InGameController inGameController;

    @Inject
    public ContactsComponent() {
    }

    @OnInit
    public void init(){
        loadEmpireWars();
    }

    private void loadEmpireWars() {
        subscriber.subscribe(warService.getWars(tokenStorage.getGameId(),tokenStorage.getEmpireId()),
                warDtos-> {
                    this.wars.clear();
                    System.out.println(warDtos);
                    this.wars.addAll(warDtos);
                });
    }

    public void closeContactsComponent() {
        inGameController.closeComponents();
    }

    @OnRender
    public void render() {
        contactsService.loadContacts();
        this.contactCells = contactsService.contacts;
        this.contactsListView.setItems(this.contactsService.contacts);
        this.contactsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.contactCellProvider));
        this.contactsListView.setOnMouseClicked(event -> {
            Contact contact = this.contactsListView.getSelectionModel().getSelectedItem();
            inGameController.openContactDetails(contact);
        });
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
