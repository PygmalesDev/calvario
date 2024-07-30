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
//    final int imagesCount = 16;
//    final ArrayList<String> flagsList = new ArrayList<>();
//    final String flagsFolderPath = "flags/flag_";
//    final String resourcesPaths = "/de/uniks/stp24/assets/";
//    int flagIndex;
//    String empireName;
//    int flagImageIndex = 0;

    List<String> hiddenEmpires = new ArrayList<>();



    @Inject
    ContactsService() {
//        for (int i = 0; i <= imagesCount; i++) {
//            this.flagsList.add(resourcesPaths + flagsFolderPath + i + ".png");
//        }

    }

    public void addEnemy(String owner, String islandID) {
        System.out.println("ISLAND :" + islandID);
        System.out.println("Adding to contacts? " + owner);
        Contact contact;
        ReadEmpireDto empireDto = islandsService.getEmpire(owner);
        if(tokenStorage.getEmpireId().equals(owner)) return;
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
            System.out.println("IN LIST ... " + contact.getEmpireName());

        }
        contact.addIsland(islandID);



        System.out.println("contacts :" + contactCells.size());
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
