package de.uniks.stp24.component.game;


import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.IslandsService;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.CreateWarDto;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ContactsService;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.WarService;
import de.uniks.stp24.ws.EventListener;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "ContactDetailsComponent.fxml")
public class  ContactDetailsComponent extends StackPane {
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
    @FXML
    ToggleButton warButton;
    @FXML
    Text warStateText;

    @SubComponent
    @Inject
    WarComponent warComponent;


    public Pane parent;

    @Inject
    public ImageCache imageCache;
    @Inject
    public IslandsService islandsService;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;

    @Inject
    WarService warService;
    @Inject
    Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    EventListener eventListener;
    @Inject
    EmpireService empireService;
    @Inject
    ContactsService contactsService;

    private InGameController inGameController;
    private Contact contact;
    private final ObservableList<WarDto> wars = FXCollections.observableArrayList();

    //TODO Check this information when opening the controller.
    //TODO Close ContactComponent and contactDetailsComponent at the same time

    @Inject
    public ContactDetailsComponent() {
        this.populationIcon = new ImageView();
        this.homeIcon = new ImageView();
        this.strengthIcon = new ImageView();

    }

    @OnInit
    public void init() {
        createWarListener();
    }

    private void createWarListener() {
        this.subscriber.subscribe(this.eventListener.listen(
            "games." + tokenStorage.getGameId() + ".wars.*.*", WarDto.class),
          event -> Platform.runLater(() -> {
              switch (event.suffix()) {
                  case "created" -> {
                      String attackerID = event.data().attacker();
                      wars.add(event.data());
                      setWarMessagePopup(event.suffix(), attackerID);
                  }
                  case "deleted" -> {
                      String attackerID = event.data().attacker();
                      wars.removeIf(w -> w._id().equals(event.data()._id()));
                      setWarMessagePopup(event.suffix(), attackerID);
                  }
              }
          }),
          error -> System.out.println("createWarListener error: " + error.getMessage())
        );
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
          contact.getDiscoveryStats().get("pop"));
        siteText.setText(resources.getString("sites") + ": " +
//          contact.getStatsAtLocation().get("sites") + "/" +
//          islandsService.getAllNumberOfSites(contact.getEmpireID()));
          contact.getDiscoveryStats().get("sites"));
        buildingsText.setText(resources.getString("buildings") + ": " +
//          contact.getStatsAtLocation().get("buildings") + "/" +
//          islandsService.getAllNumberOfBuildings(contact.getEmpireID()));
          contact.getDiscoveryStats().get("buildings"));

        calculateStrength();

        updateWarButtonText();
        checkWarSituation();
        warButton.setOnAction(event -> toggleWarState());
    }

    public void checkWarSituation() {
        subscriber.subscribe(warService.getWars(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
          warDtos -> {
              if (!warDtos.isEmpty()) {
                  for (WarDto warDto : warDtos) {
                      if (contact.getEmpireID().equals(warDto.defender()) || contact.getEmpireID().equals(warDto.attacker())) {
                          contact.setAtWarWith(true);
                          warButton.setSelected(true);
                          updateWarButtonText();
                          return;
                      }
                  }
              }
              contact.setAtWarWith(false);
              warButton.setSelected(false);
              updateWarButtonText();
          });
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
        this.parent = parent;
        this.parent.getChildren().add(this);
    }

    private void setInfo() {
        contact.setEmpireDtos(islandsService.getDevIsles());
    }

    @OnDestroy
    public void removeData() {
        this.contact = null;
    }

    private String calculateIntel(double value) {
        if (value == 0) return "none";
        if (value > 0 && value <= 30) return "low";
        if (value > 30 && value <= 60) return "medium";
        if (value > 60 && value <= 90) return "high";
        return "exactly";
    }

    private void calculateStrength() {
        islandsService.getEnemyStrength(contact.getGameOwner(), contact.getEmpireID(), this.contact);
    }

    public void calculateStrength(double value) {
        String text = "";
        if (value > 2.1) text = "mighty";
        if (value < 2.1 && value > 1.1) text = "very strong";
        if (value < 1.1 && value > 0.1) text = "strong";
        if (value < 0.1 && value > -1.1) text = "weak";
        if (value < -1.1 && value > -2.1) text = "very weak";
        System.out.println(text);
        this.strengText.setText("Str: " + text);

    }


//    public void setInGameController(InGameController inGameController) {
//        this.inGameController = inGameController;
//    }

    private void updateWarButtonText() {
        warStateText.setStyle("-fx-font-size: 12px;");
        if (warButton.isSelected()) {
            warButton.setText("Stop war");
            warStateText.setFill(Color.WHITE);
            warStateText.setText("You are at war with " + contact.getEmpireName());
        } else {
            warButton.setText("Start war");
            warStateText.setFill(Color.WHITE);
            warStateText.setText("You are at peace with " + contact.getEmpireName());
        }
    }

    private void toggleWarState() {
        updateWarButtonText();
        contact.setAtWarWith(warButton.isSelected());
        warSetUp();
    }

    private void warSetUp() {
        if (contact.isAtWarWith()) {
            startWar();
        } else {
            stopWar();
        }
    }

    private void startWar() {
        subscriber.subscribe(empireService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
          empireDto -> {
              System.out.println("initiating war");
              String defenderID = contact.getEmpireID();
              String attackerID = empireDto._id();
              String defenderName = contact.getEmpireName();
              String attackerName = empireDto.name();
              String warName = attackerName + " vs. " + defenderName;
              CreateWarDto createWarDto = new CreateWarDto(attackerID, defenderID, warName);
              System.out.println(createWarDto);
              subscriber.subscribe(warService.createWar(tokenStorage.getGameId(), createWarDto),
                result -> {
                },
                error -> System.out.println("Error: " + "1" + error.getMessage()));
          });
    }

    private void stopWar() {
        subscriber.subscribe(warService.getWars(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
          warDtos -> {
              for (WarDto warDto : warDtos) {
                  System.out.println(warDto);
                  if (contact.getEmpireID().equals(warDto.defender())) {
                      subscriber.subscribe(warService.deleteWar(tokenStorage.getGameId(), warDto._id()),
                        result -> {
                        },
                        error -> System.out.println("Error: " + error.getMessage()));
                  }
              }
          });
    }

    private void setWarMessagePopup(String messageType, String attackerID) {
        subscriber.subscribe(empireService.getEmpire(tokenStorage.getGameId(), attackerID),
          empireDto -> {
              String attackerName = empireDto.name();
              if (messageType.equals("created")) {
                  contactsService.setDeclaring(true);
                  contactsService.addEnemyAfterDeclaration(attackerID);
              } else if (messageType.equals("deleted")) {
                  contactsService.setDeclaring(false);
              }
              if (!attackerID.equals(tokenStorage.getEmpireId())) {
                  contactsService.setAttacker(attackerName);
                  contactsService.declaringToDefenderCheck(attackerID);
                  if (contactsService.isDeclaringToDefender()) {
                      System.out.println("here 1");
                      warComponent.showWarMessage(messageType);
                  }
              }
          });

    }
}
