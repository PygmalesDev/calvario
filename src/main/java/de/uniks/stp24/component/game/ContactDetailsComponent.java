package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "ContactDetailsComponent.fxml")
public class ContactDetailsComponent extends StackPane {
    @FXML
    Button closeContactDetailComponentButton;
    @FXML
    Text empireNameText;
    @FXML
    ImageView empireImageView;

    @Inject
    ImageCache imageCache;

    private InGameController inGameController;
    private Contact contact;

    @Inject
    public ContactDetailsComponent() {
    }

    public void setContactInformation(Contact contact) {
        this.contact = contact;
        empireNameText.setText(contact.getEmpireName());
        empireImageView.setImage(imageCache.get(contact.getEmpireFlag()));
    }

    public void closeContactDetailsComponent() {
        inGameController.closeContractDetails();
    }


    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
