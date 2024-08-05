package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.ContactDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.TokenStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class ContactsService {
    @Inject
    public IslandsService islandsService;
    @Inject
    public Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;

    @Inject
    public EmpireApiService empireApiService;
    @Inject
    public WarService warService;

    public final ObservableList<Contact> contacts = FXCollections.observableArrayList();

    List<String> empireIDs = new ArrayList<>();

    int flagIndex;
    String empireName;
    String attacker;
    boolean declaring;
    boolean declaringToDefender;
    final int imagesCount = 16;
    final String resourcesPaths = "/de/uniks/stp24/assets/";
    final String flagsFolderPath = "flags/flag_";
    final ArrayList<String> flagsList = new ArrayList<>();


    @Inject
    ContactsService() {
        for (int i = 0; i <= imagesCount; i++) {
            this.flagsList.add(resourcesPaths + flagsFolderPath + i + ".png");
        }
    }

    public void addEnemyAfterCollision(String owner) {
        Contact contact = new Contact();
        ReadEmpireDto empireDto = islandsService.getEmpire(owner);
        contact.setEmpireName(empireDto.name());
        contact.setEmpireFlag(flagsList.get(empireDto.flag()));
        contact.setEmpireID(owner);

        if(!contacts.stream().map(Contact::getEmpireID).toList().contains(contact.getEmpireID()) && !contact.getEmpireID().equals(tokenStorage.getEmpireId())){
            empireIDs.add(contact.getEmpireID());
            contacts.add(contact);
            saveContacts();
        }
    }

    public void addEnemyAfterDeclaration(String attackID) {
        Contact contact = new Contact();
        ReadEmpireDto empireDto = islandsService.getEmpire(attackID);
        contact.setEmpireName(empireDto.name());
        contact.setEmpireFlag(flagsList.get(empireDto.flag()));
        contact.setEmpireID(attackID);
        declaringToDefenderCheck(attackID);

        boolean alreadyInContacts = contacts.stream().map(Contact::getEmpireID).toList().contains(contact.getEmpireID());
        boolean ownContactID = contact.getEmpireID().equals(tokenStorage.getEmpireId());
        if(!alreadyInContacts && !ownContactID && isDeclaringToDefender()){
            empireIDs.add(contact.getEmpireID());
            contacts.add(contact);
            saveContacts();
        }
    }

    public void declaringToDefenderCheck(String attackID) {
        subscriber.subscribe(warService.getWars(tokenStorage.getGameId(), attackID),
                warDtos -> {
                    System.out.println(warDtos.size());
                    System.out.println(warDtos);
                    if(warDtos.size() <= 0){
                        System.out.println("A");
                        setDeclaringToDefender(true);
                    }else {
                        warDtos.sort(Comparator.comparing(WarDto::createAt).reversed());
                        WarDto latestWar = warDtos.getFirst();
                        setDeclaringToDefender(latestWar.defender().equals(tokenStorage.getEmpireId()));
                        System.out.println("B");
                    }
                }, error -> System.out.println("declaringToDefenderCheck error: " + error.getMessage()));
    }

    public void dispose(){
        subscriber.dispose();
        contacts.clear();
    }

    public void saveContacts() {
        subscriber.subscribe(this.empireApiService.getContacts(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                contactDto->{
                    if (Objects.nonNull(contactDto._private()) && Objects.nonNull(contactDto._private().get("contacts"))) {
                        contactDto._private().put("contacts", empireIDs);
                        subscriber.subscribe(this.empireApiService.saveContacts(tokenStorage.getGameId(), tokenStorage.getEmpireId(), contactDto));
                    }else {
                        subscriber.subscribe(this.empireApiService.saveContacts(tokenStorage.getGameId(), tokenStorage.getEmpireId(), new ContactDto(Map.of("contacts", empireIDs))));
                    }
                }, error -> System.out.println("errorSaveContacts:" + error.getMessage())
        );
    }


    public void loadContacts() {
        subscriber.subscribe(this.empireApiService.getContacts(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                contactDto -> {
                    this.contacts.clear();
                    if (Objects.nonNull(contactDto._private()) && Objects.nonNull(contactDto._private().get("contacts"))) {
                        ((List<String>)contactDto._private().get("contacts")).forEach(this::addEnemyAfterCollision);
                    }
                }
                , error -> System.out.println("errorLaodContacts:" + error.getMessage()));
    }

    public boolean isDeclaring() {
        return declaring;
    }

    public void setDeclaring(boolean declaring) {
        this.declaring = declaring;
    }

    public String getAttacker() {
        return attacker;
    }
    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }

    public boolean isDeclaringToDefender() {
        return declaringToDefender;
    }

    public void setDeclaringToDefender(boolean declaringToDefender) {
        this.declaringToDefender = declaringToDefender;
    }
}
