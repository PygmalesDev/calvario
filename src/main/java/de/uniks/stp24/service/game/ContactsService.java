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
    public EmpireApiService empireApiService;
    @Inject
    public Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;

    public ObservableList<Contact> contactCells = FXCollections.observableArrayList();
    final ArrayList<Contact> seenEnemies = new ArrayList<>();
    List<String> hiddenEmpires = new ArrayList<>();
    private String gameID;
    private String gameOwnerID;

    @Inject
    ContactsService() {
    }

    public void addEnemy(String enemy, String islandID) {
        //todo remove printouts
        System.out.println("ISLAND :" + islandID);
        System.out.println("Adding to contacts? " + enemy);
        Contact contact;
        ReadEmpireDto empireDto = islandsService.getEmpire(enemy);
        // if enemy's ID == game owner's ID do nothing
        if(tokenStorage.getEmpireId().equals(enemy)) return;
        // if not, check if already discovered -> add or search it
        if(hiddenEmpires.contains(enemy)) {
            hiddenEmpires.remove(enemy);
            System.out.println(empireDto.name());
            contact = new Contact(empireDto);
            contactCells.add(contact);
            seenEnemies.add(contact);
            System.out.println(contact.getEmpireName());
        } else {
            contact = seenEnemies.stream()
              .filter(element -> element.getEmpireID().equals(enemy)).findFirst().get();
            System.out.println("ALREADY IN LIST ... " + contact.getEmpireName());
        }

        contact.addIsland(islandID);
        contact.setGameOwner(gameOwnerID);
        saveContacts();

        System.out.println(Objects.nonNull(contact.getPane()) && contact.getPane().visibleProperty().get());
        // in case that a contact detail component is open, and you go to another island from same contact
        // the view will be updated
        if (Objects.nonNull(contact.getPane()) && contact.getPane().visibleProperty().get()) contact.getPane().setContactInformation(contact);

        System.out.println("contacts you've seen: " + contactCells.size());
    }

    @OnDestroy
    public void dispose() {
        System.out.println("saved info of " + this.gameID);
        saveContacts();
        subscriber.dispose();
        contactCells.clear();
        seenEnemies.clear();
//        this.gameID = null;
//        if(Objects.nonNull(savedContacts) && Objects.nonNull(savedContacts._private())) loadContacts();
    }

    public void getEmpiresInGame() {
        this.gameID = tokenStorage.getGameId();
        this.gameOwnerID = tokenStorage.getEmpireId();
        this.hiddenEmpires = new ArrayList<>(islandsService.getEmpiresID());
        this.hiddenEmpires.remove(this.gameOwnerID);
        System.out.println("game " + this.gameID + ". Enemies not discovered yet " + this.hiddenEmpires);
        loadContactsData();
    }

    // todo save on server!
    // after talking with other devs: the empireID in a game is clearly
    // that means that my Empire in game 1 has another empireID as in game 2
    // for warStatus must be additionally a character or word at end

    private Map<String, Object> mapContacts(Map<String, Object> map) {
        Map<String, Object> tmp = map.isEmpty() ? new HashMap<>() : map;
        for (Contact enemy : seenEnemies) {
            tmp.put(enemy.getEmpireID(), enemy.getDiscoveredIslands());
        }
        return tmp;
    }

    public void saveContacts() {
        this.subscriber.subscribe(this.empireApiService.getPrivate(this.gameID, this.gameOwnerID),
          result -> {
              final Map<String, Object> newPrivate = new HashMap<>(
                mapContacts(Objects.nonNull(result._private()) ?
                  result._private() : new HashMap<>()));
              subscriber.subscribe(this.empireApiService.savePrivate(this.gameID, this.gameOwnerID,new EmpirePrivateDto(newPrivate)),
                saved -> System.out.println(saved._private()),
                error -> System.out.println("error while saving contacts....") );
          },
          error -> System.out.println("error while saving contacts")
        );
    }

    //todo retrieve data from server
    public void loadContactsData() {
        this.subscriber.subscribe(this.empireApiService.getPrivate(this.gameID,this.gameOwnerID),
          result -> {
            System.out.println(result);
            loadContacts(result._private());},
          error -> System.out.println("error while loading contacts"));
    }

    public void loadContacts(Map<String, Object> map) {
        if (!map.isEmpty()) {
            Map<String, List<String>> tmp = new HashMap<>();
            for (String key : map.keySet()) {
                System.out.println(key + " -> " + map.get(key).getClass());
                if (map.get(key) instanceof List<?> value ) {
                    tmp.put(key,(List<String>) value);
                }
            }

            System.out.println("loaded data " + tmp);
            recreateContacts(tmp);
        }
    }

    public void recreateContacts(Map<String, List<String>> data) {
        if (data.isEmpty()) return;
        for (String key : data.keySet()) {
            data.get(key).forEach(id -> this.addEnemy(key,id));
        }
    }

}