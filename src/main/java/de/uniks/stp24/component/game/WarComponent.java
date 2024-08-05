package de.uniks.stp24.component.game;

import de.uniks.stp24.service.game.ContactsService;
import de.uniks.stp24.service.game.WarService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

@Component(view = "War.fxml")
public class WarComponent extends AnchorPane {
    @FXML
    Button closeButton;
    @FXML
    Text warText;
    @Inject
    Subscriber subscriber;
    @Inject
    WarService warService;
    @Inject
    ContactsService contactsService;

    private ContactDetailsComponent contactDetailsComponent;

    @Inject
    public WarComponent() {

    }

    @OnInit
    public void init() {

    }

    @OnRender
    public void render() {
        setText();
    }

    public void setText() {
        if (contactsService.isDeclaring()) {
            warText.setText("The " + contactsService.getAttacker() + " have started a war against you!");
        } else {
            warText.setText("The " + contactsService.getAttacker() + " have stopped the war against you!");
        }
        contactsService.setDeclaring(false);
    }

    public void close() {
        setVisible(false);
    }

}
