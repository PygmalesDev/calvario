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
    public ImageView populationIcon;
    @FXML
    public Text siteText;
    @FXML
    public Text buildingsText;
    @FXML
    public ImageView homeIcon;
    @FXML
    public Text strengText;
    @FXML
    public ImageView strengthIcon;
    @FXML
    Button closeContactDetailComponentButton;
    @FXML
    Text empireNameText;
    @FXML
    ImageView empireImageView;

    public Pane parent;

    @Inject
    public ImageCache imageCache;
    @Inject
    public IslandsService islandsService;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;

    public Contact contact;


    @Inject
    public ContactDetailsComponent() {
        this.populationIcon = new ImageView();
        this.homeIcon = new ImageView();
        this.strengthIcon = new ImageView();

    }

    public void setContactInformation(Contact contact) {

        this.contact = contact;
        contact.checkIslands();
        setInfo();
//        intelText.setText("this isle " + contact.getAtIsland().substring(20));
        intelText.setText("Intel: " + calculateIntel(contact.getIntel()));
        empireNameText.setText(contact.getEmpireName());
        empireImageView.setImage(imageCache.get(contact.getEmpireFlag()));
        populationIcon.setImage(imageCache.get("icons/resources/population.png"));
        homeIcon.setImage(imageCache.get("assets/contactsAndWars/home.png"));
        strengthIcon.setImage(imageCache.get("assets/contactsAndWars/cannon.png"));
        popText.setText(resources.getString("pop") + ": " +
//          contact.getStatsAtLocation().get("pop") + "/" +
          contact.getDiscoveryStats().get("pop") );
        siteText.setText(resources.getString("sites") + ": " +
//          contact.getStatsAtLocation().get("sites") + "/" +
//          islandsService.getAllNumberOfSites(contact.getEmpireID()));
          contact.getDiscoveryStats().get("sites"));
        buildingsText.setText(resources.getString("buildings") + ": " +
//          contact.getStatsAtLocation().get("buildings") + "/" +
//          islandsService.getAllNumberOfBuildings(contact.getEmpireID()));
          contact.getDiscoveryStats().get("buildings"));

        calculateStrength();

    }

    public void closeContactDetailsComponent() {
        this.getParent().visibleProperty().setValue(false);
        visibleProperty().setValue(false);
    }

    public void openDetail(Contact contact) {
        setContactInformation(contact);
        this.getParent().visibleProperty().setValue(true);
        visibleProperty().setValue(true);
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

    private void calculateStrength(){
        islandsService.getEnemyStrength(contact.getGameOwner(), contact.getEmpireID(), this.contact);
    }

    public void calculateStrength(double value) {
        String text = "";
        if (value > 2.1) text = "mighty";
        if (value < 2.1 && value > 1.1) text = "very strong";
        if (value < 1.1 && value > 0.1) text = "strong";
        if (value < 0.1 && value > -1.1) text = "weak";
        if (value < -1.1 && value > -2.1) text = "very weak"; System.out.println(text);
        this.strengText.setText("Str: " + text);

    }
}