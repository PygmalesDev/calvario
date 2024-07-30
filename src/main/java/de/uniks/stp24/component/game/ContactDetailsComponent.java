package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.IslandsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "ContactDetailsComponent.fxml")
public class ContactDetailsComponent extends StackPane {
    @FXML
    public Text intelText;
    @FXML
    public Text popText;
    @FXML
    public Text homeText;
    @FXML
    public ImageView iconPop;
    @FXML
    public Text siteText;
    @FXML
    public Text buildingsText;
    @FXML
    public ImageView iconHome;
    @FXML
    public Text strengText;
    @FXML
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
    @Inject
    IslandsService islandsService;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;

    private Contact contact;


    @Inject
    public ContactDetailsComponent() {
        this.iconPop = new ImageView();
        this.iconHome = new ImageView();
        this.iconStreng = new ImageView();

    }

    public void setContactInformation(Contact contact) {

        this.contact = contact;
        contact.checkIslands();
        setInfo();
        intelText.setText("Intel: " + calculateIntel(contact.getIntel()));
        empireNameText.setText(contact.getEmpireName());
        empireImageView.setImage(imageCache.get(contact.getEmpireFlag()));
        iconPop.setImage(imageCache.get("icons/resources/population.png"));
        iconHome.setImage(imageCache.get("assets/contactsAndWars/home.png"));
        iconStreng.setImage(imageCache.get("assets/contactsAndWars/cannon.png"));

        popText.setText(resources.getString("pop") + ": " +
          contact.getDiscoveredPopulation() + "/" + contact.getEmpirePopulation() );
        siteText.setText(resources.getString("sites") + ": " +
          islandsService.getAllNumberOfSites(contact.getEmpireID()));
        buildingsText.setText(resources.getString("buildings") + ": " +
          islandsService.getAllNumberOfBuildings(contact.getEmpireID()));

    }

    public void closeContactDetailsComponent() {
        this.getParent().setVisible(false);
    }

    public void openDetail(Contact contact) {
        setContactInformation(contact);

        this.getParent().setVisible(true);
        setVisible(true);

    }

    public void setParent(Pane parent) {
        this.parent = parent ;
        this.parent.getChildren().add(this);
    }

    private void setInfo() {
        contact.setEmpireDtos(islandsService.getDevIsles());

    }

    @OnDestroy
    public void removeData(){
        this.contact = null;
    }

    private String calculateIntel(double value) {
        if (value == 0) return "none";
        if (value > 0 && value <=30) return "low";
        if (value > 30 && value <=60) return "medium";
        if (value > 60 && value <= 90) return "high";
        return "exactly";
    }




}
