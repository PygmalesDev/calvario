package de.uniks.stp24.component.game;

import com.fasterxml.jackson.databind.node.ArrayNode;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.CreateWarDto;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

@Component(view = "ContactDetailsComponent.fxml")
public class ContactDetailsComponent extends StackPane {
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

    @Inject
    ImageCache imageCache;
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

    private InGameController inGameController;
    private Contact contact;
    private final ObservableList<WarDto> wars = FXCollections.observableArrayList();

    //TODO Check this information when opening the controller.
    //TODO Close ContactComponent and contactDetailsComponent at the same time

    @Inject
    public ContactDetailsComponent() {
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
                        case "created" -> wars.add(event.data());
                        case "update" -> wars.replaceAll(w -> w._id().equals(event.data()._id()) ? event.data() : w);
                        case "deleted" -> wars.removeIf(w -> w._id().equals(event.data()._id()));
                    }
                }),
                error -> System.out.println("createWarListener error: " + error.getMessage())
        );
    }

    public void setContactInformation(Contact contact) {
        this.contact = contact;
        empireNameText.setText(contact.getEmpireName());
        empireImageView.setImage(imageCache.get(contact.getEmpireFlag()));
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
        inGameController.closeContractDetails();
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

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
        if(contact.isAtWarWith()){
            startWar();
        }else {
            stopWar();
        }
    }

    private void startWar() {
        subscriber.subscribe(empireService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                empireDto -> {
                    String defenderID = contact.getEmpireID();
                    String attackerID = empireDto._id();
                    String attackerName = empireDto.name();
                    String defenderName = contact.getEmpireName();
                    String warName = defenderName + " vs. " + attackerName;
                    CreateWarDto createWarDto = new CreateWarDto(attackerID, defenderID, warName);
                    subscriber.subscribe(warService.createWar(tokenStorage.getGameId(), createWarDto),
                            result -> {
                            },
                            error -> System.out.println("Error: " + error.getMessage()));
                });
    }

    private void stopWar() {
        subscriber.subscribe(warService.getWars(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                warDtos -> {
                    for (WarDto warDto : warDtos) {
                        System.out.println("contact.getEmpireID: "+contact.getEmpireID());
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

    private void loadContacts() {

    }
}
