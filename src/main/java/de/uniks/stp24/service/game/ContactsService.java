package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpirePrivateDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.TokenStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.annotation.event.OnDestroy;
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

    public ObservableList<Contact> contactCells = FXCollections.observableArrayList();
    final ArrayList<Contact> seenEnemies = new ArrayList<>();
    List<String> hiddenEmpires = new ArrayList<>();
    EmpirePrivateDto savedContacts;
    private String gameID;
    private String ownerID;

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
        saveContacts();

        System.out.println(Objects.nonNull(contact.getPane()) && contact.getPane().visibleProperty().get());
        // in case that a contact detail component is open and you go to another island from same contact
        // the view will be updated
        if (Objects.nonNull(contact.getPane()) && contact.getPane().visibleProperty().get()) contact.getPane().setContactInformation(contact);

        System.out.println("contacts you've seen: " + contactCells.size());
    }

    @OnDestroy
    public void dispose(){
        System.out.println("saved info of " + this.gameID);
        saveContacts();
        subscriber.dispose();
        contactCells.clear();
        seenEnemies.clear();
//        this.gameID = null;
//        if(Objects.nonNull(savedContacts) && Objects.nonNull(savedContacts._private())) loadContacts();
    }

    public void getEmpiresInGame(){
        this.gameID = tokenStorage.getGameId();
        this.ownerID = tokenStorage.getEmpireId();
        this.hiddenEmpires = new ArrayList<>(islandsService.getEmpiresID());
        this.hiddenEmpires.remove(this.ownerID);
        System.out.println("game " + this.gameID + ". Enemies not discovered yet " + this.hiddenEmpires);
//        loadContactsData();
    }

    private Map<String, Object> mapContacts(Map<String, Object> map) {
        Map<String, Object> tmp = map.isEmpty() ? new HashMap<>() : map;
        for (Contact enemy : seenEnemies) {
            tmp.put(this.gameID+enemy.getEmpireID(), enemy.getDiscoveredIslands());
            tmp.put(this.gameID+enemy.getEmpireID()+"warStatus", false); //
        }
        return tmp;
    }

    // todo save on server!
    // I think it's necessary to use as key a concat form gameid+empireid
    // for warStatus must be additionally a character or word at end
    // this way is clearly to which game the contact does belong
    public void saveContacts() {
        this.subscriber.subscribe(this.empireApiService.getPrivate(this.gameID, this.ownerID),
          result -> {
              final Map<String, Object> newPrivate = new HashMap<>(
                mapContacts(Objects.nonNull(result._private()) ?
                result._private() : new HashMap<>()));
              System.out.println(newPrivate);
            subscriber.subscribe(this.empireApiService.savePrivate(this.gameID, this.ownerID,new EmpirePrivateDto(newPrivate)),
              saved -> System.out.println(saved._private()),
              error -> System.out.println("error while saving contacts....") );
          },
          error -> System.out.println("error while saving contacts")

        );
        /*Map<String, Object> tmp = new HashMap<>();
        for (Contact enemy : seenEnemies) {
            tmp.put(this.gameID+enemy.getEmpireID(), enemy.getDiscoveredIslands());
            tmp.put(this.gameID+enemy.getEmpireID()+"warStatus", false); //
        }
        savedContacts = new EmpirePrivateDto(tmp);*/
    }

    public void loadContactsData(){
        this.subscriber.subscribe(this.empireApiService.getPrivate(this.gameID,this.ownerID),
          result -> {},
          error -> {});
    }

    public void loadContacts() {
        //subscribe....
        Map<String,Object> loaded = savedContacts._private(); // assign correct map
        boolean matchGame;
        int length = gameID.length();
        if (!loaded.isEmpty()) {
            Map<String, List<String>> tmp = new HashMap<>();
            for (String key : loaded.keySet()) {
                System.out.println(loaded.get(key).getClass());
                System.out.println(key.substring(0, length));
                System.out.println(key.substring(length));
                matchGame = key.substring(0, length).equals(gameID);
                if (matchGame && loaded.get(key) instanceof List<?> value ) {
                    tmp.put(key.substring(length,2 * length),(List<String>) value);
                } else if (key.substring(0, length).equals(gameID) && loaded.get(key) instanceof Boolean bool ) {
                    System.out.println( key.substring(length,2 * length) + " -> not " + !bool);
                }
            }
            System.out.println("loaded data " + tmp);
        }
    }

    public void recreateContacts(Map<String, List<String>> data) {
        if (data.isEmpty()) return;
        getEmpiresInGame();
        for (String key : data.keySet()) {
            data.get(key).forEach(id -> this.addEnemy(key,id));
        }
    }


    public void infoEmpire(){}
}