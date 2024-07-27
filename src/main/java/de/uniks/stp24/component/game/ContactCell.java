package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.control.Cell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "ContactCell.fxml")
public class ContactCell extends HBox implements ReusableItemComponent<Contact> {
    @FXML
    Text empireNameText;
    @FXML
    ImageView empireFlagImageView;


    @Inject
    ImageCache imageCache;

    private Contact contact;

    @Inject
    public ContactCell() {
    }

    @Override
    public void setItem(@NotNull Contact contact) {
        this.contact = contact;
        empireNameText.setText(this.contact.getEmpireName());
        empireFlagImageView.setImage(imageCache.get(this.contact.getEmpireFlag()));

    }

    public void openDetail() {
        System.out.println(contact);
    }
}
