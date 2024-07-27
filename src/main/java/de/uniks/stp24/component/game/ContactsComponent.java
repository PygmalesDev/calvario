package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.game.ContactsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

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
    Provider<ContactCell> contactCellProvider;

    public ObservableList<Contact> contactCells = FXCollections.observableArrayList();
    private InGameController inGameController;

    @Inject
    public ContactsComponent() {
    }

    @OnInit
    public void init(){

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

//            applyInputs(gang);
//            traitsBox.setVisible(false);
//            changeEditNodes(false, false);
//            showCreationButton.setVisible(true);
        });
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
