package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.game.ContactsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Provider;

@Component(view = "ContactsComponent.fxml")
public class ContactsComponent extends StackPane {
    @FXML
    ListView<Contact> contactsListView;

    @SubComponent
    @Inject
    public ContactDetailsComponent contactDetailsComponent;

    public Pane parent;
    public Pane parentDetails;

    @Inject
    App app;
    @Inject
    ContactsService contactsService;
    @Inject
    Provider<ContactCell> contactCellProvider;

    public final ObservableList<Contact> contactCells = FXCollections.observableArrayList();
//    private InGameController inGameController;

    @Inject
    public ContactsComponent() {
    }

    public void closeContactsComponent() {
        this.setVisible(false);
    }

    @OnRender
    public void render() {
        this.contactsListView.setItems(this.contactsService.contactCells);
        this.contactsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.contactCellProvider));

        this.contactsListView.setOnMouseClicked(event -> {
            Contact contact = this.contactsListView.getSelectionModel().getSelectedItem();
//            contactDetailsComponent.setContactInformation(contact);
            contactDetailsComponent.openDetail(contact);




//            applyInputs(gang);
//            traitsBox.setVisible(false);
//            changeEditNodes(false, false);
//            showCreationButton.setVisible(true);
        });
    }

    public void setParents(Pane ownParent,  Pane detailParent) {
        this.parent = ownParent;
        this.parentDetails = detailParent;
        this.contactDetailsComponent.setParent(detailParent);

    }

    //remove
//    public void setInGameController(InGameController inGameController) {
//        this.inGameController = inGameController;
//    }
}
