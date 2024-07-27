package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.model.SeasonComponent;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.TokenStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class ContactsService {
    @Inject
    public IslandsService islandsService;
    @Inject
    public Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;

    public ObservableList<Contact> contactCells = FXCollections.observableArrayList();
    int flagIndex;
    String empireName;
    final int imagesCount = 16;
    int flagImageIndex = 0;
    final String resourcesPaths = "/de/uniks/stp24/assets/";
    final String flagsFolderPath = "flags/flag_";
    final ArrayList<String> flagsList = new ArrayList<>();


    @Inject
    ContactsService() {
        for (int i = 0; i <= imagesCount; i++) {
            this.flagsList.add(resourcesPaths + flagsFolderPath + i + ".png");
        }
    }

    public void addEnemy(String owner) {
        System.out.println("Adding to contacts");
        Contact contact = new Contact();

        ReadEmpireDto empireDto = islandsService.getEmpire(owner);

        System.out.println(empireDto);
        contact.setEmpireName(empireDto.name());
        contact.setEmpireFlag(flagsList.get(empireDto.flag()));

        boolean alreadIn = false;

        for (Contact contactCell : contactCells) {
            if(contactCell.getEmpireName().equals(contact.getEmpireName())) {
                alreadIn =  true;
                break;
            }
        }

        if(!alreadIn) {
            contactCells.add(contact);
        }

        System.out.println(islandsService.getAllNumberOfSites(empireDto._id()));
        System.out.println(islandsService.getAllNumberOfBuildings(empireDto._id()));
        System.out.println(contact.getEmpireName());
        System.out.println(contactCells.size());
    }


    public void dispose(){
        subscriber.dispose();
        contactCells.clear();
    }
}
