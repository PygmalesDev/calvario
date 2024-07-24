package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "ContactsComponent.fxml")
public class ContactsComponent extends StackPane {
    @FXML
    ListView<ContactCell> contactsList;


    @Inject
    public ContactsComponent(){

    }
}
