package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.ContactsService;
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
    public ListView<Contact> contactsListView;

    @SubComponent
    @Inject
    public ContactDetailsComponent contactDetailsComponent;

    public Pane parent;
    public Pane parentDetails;

    @Inject
    public App app;
    @Inject
    public ContactsService contactsService;
    @Inject
    public ImageCache imageCache;

    public Provider<ContactCell> contactCellProvider = () -> {
        var cell = new ContactCell(this.imageCache, this.contactDetailsComponent);
        cell.setOnMouseClicked(event -> {
            cell.getContact().setPane(this.contactDetailsComponent);
            contactDetailsComponent.openDetail(cell.getContact());
        });
        return cell;
    };

    @Inject
    public ContactsComponent() {
    }

    public void closeContactsComponent() {
        this.contactDetailsComponent.closeContactDetailsComponent();
        this.setVisible(false);
    }

    @OnRender
    public void render() {
        this.contactsListView.setItems(this.contactsService.contactCells);
        this.contactsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.contactCellProvider));

    }

    public void setParents(Pane ownParent,  Pane detailParent) {
        this.parent = ownParent;
        this.parentDetails = detailParent;
        this.contactDetailsComponent.setParent(detailParent);
    }

}
