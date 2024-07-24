package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.game.ContactsService;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Objects;

@Component(view = "ContactsComponent.fxml")
public class ContactsComponent extends StackPane {
    @FXML
    ListView<Contact> contactsListView;

    @Inject
    App app;
    @Inject
    Provider<ContactCell> contactCellProvider;
    @Inject
    ContactsService contactsService;

    public final ObservableList<Contact> contactCells =  FXCollections.observableArrayList();

    @Inject
    public ContactsComponent(){
    }

    public void closeContactsComponent(){
        this.setVisible(false);
    }

    @OnRender
    public void render(){
        this.contactsListView.setItems(this.contactsService.contactCells);
        this.contactsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.contactCellProvider));
    }
}
