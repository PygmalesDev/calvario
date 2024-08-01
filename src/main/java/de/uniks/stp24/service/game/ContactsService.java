package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.TokenStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Singleton
public class ContactsService {
    @Inject
    public IslandsService islandsService;
    @Inject
    public Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;

    public ObservableList<Contact> contactCells = FXCollections.observableArrayList();
    final ArrayList<Contact> seenEnemies = new ArrayList<>();
    List<String> hiddenEmpires = new ArrayList<>();

    @Inject
    ContactsService() {
    }

    public void addEnemy(String owner, String islandID) {
        //todo remove printouts
        System.out.println("ISLAND :" + islandID);
        System.out.println("Adding to contacts? " + owner);
        Contact contact;
        ReadEmpireDto empireDto = islandsService.getEmpire(owner);
        // if enemy's ID == game owner's ID do nothing
        if(tokenStorage.getEmpireId().equals(owner)) return;
        // if not check if already discovered -> add or search it
        if(hiddenEmpires.contains(owner)) {
            hiddenEmpires.remove(owner);
            System.out.println(empireDto.name());
            contact = new Contact(empireDto);
            contactCells.add(contact);
            seenEnemies.add(contact);
            System.out.println(contact.getEmpireName());
        } else {
            contact = seenEnemies.stream()
              .filter(element -> element.getEmpireID().equals(owner)).findFirst().get();
            System.out.println("ALREADY IN LIST ... " + contact.getEmpireName());
        }

        contact.addIsland(islandID);
        contact.setGameOwner(tokenStorage.getEmpireId());

        System.out.println(Objects.nonNull(contact.getPane()) && contact.getPane().visibleProperty().get());
        // in case that a contact detail component is open and you go to another island from same contact
        // the view will be updated
        if (Objects.nonNull(contact.getPane()) && contact.getPane().visibleProperty().get()) contact.getPane().setContactInformation(contact);

        System.out.println("contacts you've seen: " + contactCells.size());
    }

    @OnDestroy
    public void dispose(){
        subscriber.dispose();
        contactCells.clear();
    }

    public void getEmpiresInGame(){
        this.hiddenEmpires = new ArrayList<>(islandsService.getEmpiresID());
        this.hiddenEmpires.remove(tokenStorage.getEmpireId());
        System.out.println("not discovered yet " + this.hiddenEmpires);

    }

}
