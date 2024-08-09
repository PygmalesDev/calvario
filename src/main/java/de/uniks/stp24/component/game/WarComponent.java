package de.uniks.stp24.component.game;

import de.uniks.stp24.dto.WarDto;
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
    ContactsService contactsService;

    private ContactDetailsComponent contactDetailsComponent;
    private StackPane parent;

    @Inject
    public WarComponent() {

    }

    public void close() {
        parent.setVisible(false);
        setVisible(false);
    }

    public void showWarMessage(String msg, String attacker) {
        String text = "The " + attacker + " have" + (
          msg.equals("created") ? " started" : " stopped"
          ) + " the against you";
        warText.setText(text);
        parent.setVisible(true);
        setVisible(true);
    }

    public void setParent(StackPane parent) {
        this.parent = parent;
        this.parent.setVisible(true);
        parent.getChildren().add(this);
    }

}
