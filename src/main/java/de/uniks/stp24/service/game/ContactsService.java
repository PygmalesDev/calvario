package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.EmpirePrivateDto;
import de.uniks.stp24.dto.ContactDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.WarDto;
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
    public TokenStorage tokenStorage;

    public ObservableList<Contact> contactCells = FXCollections.observableArrayList();
    public final ArrayList<Contact> seenEnemies = new ArrayList<>();
    public List<String> hiddenEmpires = new ArrayList<>();
    public String gameID;
    public String gameOwnerID;


    @Inject
    public WarService warService;
        //todo contacts - > contactsCells
//    public final ObservableList<Contact> contacts = FXCollections.observableArrayList();

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

        //check
//        if(!contacts.stream().map(Contact::getEmpireID).toList().contains(contact.getEmpireID()) && !contact.getEmpireID().equals(tokenStorage.getEmpireId())){
//            empireIDs.add(contact.getEmpireID());
//            contacts.add(contact);

//        }
    }

    @OnDestroy
    public void dispose() {
        subscriber.dispose();
        contactCells.clear();
        seenEnemies.clear();
            this.gameID = null;}

//    public void addEnemyAfterCollision(String owner) {
////        Contact contact = new Contact();
////        ReadEmpireDto empireDto = islandsService.getEmpire(owner);
//
//
//        if(!contacts.stream().map(Contact::getEmpireID).toList().contains(contact.getEmpireID()) && !contact.getEmpireID().equals(tokenStorage.getEmpireId())){
//            empireIDs.add(contact.getEmpireID());
//            contacts.add(contact);
//            saveContacts();
//        }
//    }

    public void addEnemyAfterDeclaration(String attackID) {
        Contact contact;
        ReadEmpireDto empireDto = islandsService.getEmpire(attackID);
        contact = new Contact(empireDto);
        declaringToDefenderCheck(attackID);

        boolean alreadyInContacts = contactCells.stream().map(Contact::getEmpireID).toList().contains(contact.getEmpireID());
        boolean ownContactID = contact.getEmpireID().equals(tokenStorage.getEmpireId());
        if(!alreadyInContacts && !ownContactID && isDeclaringToDefender()){
            empireIDs.add(contact.getEmpireID());
            contactCells.add(contact);
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




//
//    public void loadContacts() {
//        subscriber.subscribe(this.empireApiService.getContacts(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
//                contactDto -> {
//                    this.contacts.clear();
//                    if (Objects.nonNull(contactDto._private()) && Objects.nonNull(contactDto._private().get("contacts"))) {
//                        ((List<String>)contactDto._private().get("contacts")).forEach(this::addEnemyAfterCollision);
//                    }
//                }
//                , error -> System.out.println("errorLaodContacts:" + error.getMessage()));
//    }

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