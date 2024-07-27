package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Component(view = "ContactDetailsComponent.fxml")
public class ContactDetailsComponent extends StackPane {
    public Label intelText;
    public Text popText;
    public Text homeText;
    public ImageView iconPop;
    public Text siteText;
    public Text buildingsText;
    public ImageView iconHome;
    public Text strengText;
    public ImageView iconStreng;
    @FXML
    Button closeContactDetailComponentButton;
    @FXML
    Text empireNameText;
    @FXML
    ImageView empireImageView;

    public Pane parent;

    @Inject
    ImageCache imageCache;

    private InGameController inGameController;
    private Contact contact;

    @Inject
    public ContactDetailsComponent() {
        this.iconPop = new ImageView();
        this.iconHome = new ImageView();
        this.iconStreng = new ImageView();

    }

    public void setContactInformation(Contact contact) {
        this.contact = contact;
        empireNameText.setText(contact.getEmpireName());
        empireImageView.setImage(imageCache.get(contact.getEmpireFlag()));
        iconPop.setImage(imageCache.get("icons/resources/population.png"));
        iconHome.setImage(imageCache.get("icons/resources/population.png"));
        iconStreng.setImage(imageCache.get("icons/resources/population.png"));


        System.out.println("applying contact: " + contact.getEmpireName());
    }

    public void closeContactDetailsComponent() {
        System.out.println("close window");
        this.getParent().setVisible(false);
    }


    public void openDetail(Contact contact) {
        setContactInformation(contact);
        this.getParent().setVisible(true);
        System.out.println(empireNameText.getText());
    }

    public void setParent(Pane parent) {
        this.parent = parent ;
        this.parent.getChildren().add(this);
    }







// //   public void setInGameController(InGameController inGameController) {
//        this.inGameController = inGameController;
//    }
}
