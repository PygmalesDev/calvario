package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.ContactsComponent;
import de.uniks.stp24.dto.CreateWarDto;
import de.uniks.stp24.dto.EmpirePrivateDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
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
    @Inject
    EventListener eventListener;
    @Inject
    public WarService warService;

    public ObservableList<Contact> contactCells = FXCollections.observableArrayList();
    public final ArrayList<Contact> seenEnemies = new ArrayList<>();
    public List<String> hiddenEmpires = new ArrayList<>();
    public String gameID;
    public String myOwnEmpireID;
    Map<String, WarDto> warsOnProcess = new HashMap<>();
    private final ObservableList<WarDto> warsInThisGame = FXCollections.observableArrayList();


    String attacker;
    boolean declaring;
    boolean declaringToDefender;

    ContactsComponent contactsComponent;


    @Inject
    ContactsService() {

    }

    @OnDestroy
    public void dispose() {
        subscriber.dispose();
        contactCells.clear();
        seenEnemies.clear();
        this.gameID = null;
    }


    // add enemy after discovered an island of his empire
    public void addEnemy(String enemy, String islandID) {
        System.out.println("ISLAND :" + islandID + " Adding to contacts? " + enemy);
        Contact contact;
        ReadEmpireDto empireDto = islandsService.getEmpire(enemy);
        // if enemy's ID == game owner's ID do nothing
        if(tokenStorage.getEmpireId().equals(enemy)) return;
        // if not, check if already discovered -> add or search it
        if(hiddenEmpires.contains(enemy)) {
            hiddenEmpires.remove(enemy);
            contact = new Contact(empireDto);
            contactCells.add(contact);
            seenEnemies.add(contact);
        } else {
            contact = seenEnemies.stream()
              .filter(element -> element.getEmpireID().equals(enemy)).findFirst().get();
            System.out.println("ALREADY IN LIST ... " + contact.getEmpireName());
        }

        contact.addIsland(islandID);
        contact.setMyOwnId(myOwnEmpireID);
        saveContacts();

        // in case that a contact detail component is open, and you go to another island from same contact
        // the view will be updated
        if (Objects.nonNull(contact.getPane()) && contact.getPane().visibleProperty().get()) contact.getPane().setContactInformation(contact);

        // for what was this check
//        if(!contacts.stream().map(Contact::getEmpireID).toList().contains(contact.getEmpireID()) && !contact.getEmpireID().equals(tokenStorage.getEmpireId())){
//            empireIDs.add(contact.getEmpireID());
//            contacts.add(contact);

//        }
    }

    public void addEnemyAfterDeclaration(String enemy) {
        ReadEmpireDto empireDto = islandsService.getEmpire(enemy);
        Contact contact = new Contact(empireDto);
        declaringToDefenderCheck(enemy);
        hiddenEmpires.remove(enemy);
        contactCells.add(contact);
        seenEnemies.add(contact);
        contact.setMyOwnId(myOwnEmpireID);
        saveContacts();

        System.out.println("enemy added after war declaration");
    }

    public void declaringToDefenderCheck(String attackID) {
        subscriber.subscribe(warService.getWars(tokenStorage.getGameId(), attackID),
                warDtos -> {
                    System.out.println(warDtos.size());
                    System.out.println(warDtos);
                    if(warDtos.size() <= 0){
                        System.out.println("A");
                        setDeclaringToDefender(true);
                    } else {
                        warDtos.sort(Comparator.comparing(WarDto::createAt).reversed());
                        WarDto latestWar = warDtos.getFirst();
                        setDeclaringToDefender(latestWar.defender().equals(tokenStorage.getEmpireId()));
                        System.out.println("B");
                    }
                }, error -> System.out.println("declaringToDefenderCheck error: " + error.getMessage()));
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

    public void getEmpiresInGame() {
        this.gameID = tokenStorage.getGameId();
        this.myOwnEmpireID = tokenStorage.getEmpireId();
        this.hiddenEmpires = new ArrayList<>(islandsService.getEmpiresID());
        this.hiddenEmpires.remove(this.myOwnEmpireID);
        System.out.println("game " + this.gameID + ". Enemies not discovered yet " + this.hiddenEmpires);
        loadContactsData();
        createWarListener();
    }

    private Map<String, Object> mapContacts(Map<String, Object> map) {
        Map<String, Object> tmp = map.isEmpty() ? new HashMap<>() : map;
        for (Contact enemy : seenEnemies) {
            tmp.put(enemy.getEmpireID(), enemy.getDiscoveredIslands());
        }
        return tmp;
    }

    public void saveContacts() {
        this.subscriber.subscribe(this.empireApiService.getPrivate(this.gameID, this.myOwnEmpireID),
          result -> {
              final Map<String, Object> newPrivate = new HashMap<>(
                mapContacts(Objects.nonNull(result._private()) ?
                  result._private() : new HashMap<>()));
              subscriber.subscribe(this.empireApiService.savePrivate(this.gameID, this.myOwnEmpireID,new EmpirePrivateDto(new HashMap<>())),
                saved -> { },
                error -> System.out.println("error while saving contacts....") );
          },
          error -> System.out.println("error while saving contacts")
        );
    }

    public void loadContactsData() {
        this.subscriber.subscribe(this.empireApiService.getPrivate(this.gameID,this.myOwnEmpireID),
          result -> {
//            System.out.println(result);
            loadContacts(result._private());},
          error -> System.out.println("error while loading contacts"));
    }

    public void loadContacts(Map<String, Object> map) {
        if (!map.isEmpty()) {
            Map<String, List<String>> tmp = new HashMap<>();
            for (String key : map.keySet()) {
                System.out.println("contact loaded has " + map.get(key).getClass());
                if (hiddenEmpires.contains(key) && map.get(key) instanceof List<?> value ) {
                    System.out.println(value.isEmpty());
                    tmp.put(key,value.isEmpty() ? new ArrayList<String>() : (ArrayList<String>) value);
                }
            }
            System.out.println("loaded data " + tmp);
            recreateContacts(tmp);
        }
    }

    public void recreateContacts(Map<String, List<String>> data) {
        if (data.isEmpty()) return;
        for (String key : data.keySet()) {
            if (data.get(key).isEmpty()) this.addEnemyAfterDeclaration(key);
            else data.get(key).forEach(id -> this.addEnemy(key, id));
        }
    }

    public void createWarListener() {
        this.subscriber.subscribe(this.eventListener
          .listen("games." + tokenStorage.getGameId() + ".wars.*.*", WarDto.class),
          event -> {
            switch (event.suffix()) {
                case "created" -> {
                    System.out.println("contact war!");
                    warsInThisGame.add(event.data());
                }
                case "deleted" -> {
                    System.out.println("contact peace!");
                    warsInThisGame.removeIf(w -> w._id().equals(event.data()._id()));
                }
                default -> System.out.println("contact still war!");
            }
              System.out.println("att -> " + event.data().attacker() + " def -> " + event.data().defender());
              System.out.println("already seen? " + !hiddenEmpires.contains(event.data().attacker()));
            if (hiddenEmpires.contains(event.data().attacker())) addEnemyAfterDeclaration(event.data().attacker());
            this.contactsComponent.contactDetailsComponent.checkWarSituation();
              System.out.println(Objects
                .nonNull(
                  islandsService.getEmpire(event.data().attacker()).name()));
            String attackerName = islandsService.getEmpire(event.data().attacker()).name();
              System.out.println(attackerName);
            this.contactsComponent.contactDetailsComponent.setWarMessagePopup(event.suffix(), attackerName, event.data().attacker());
            System.out.println(event.data().attacker() + " and " + event.data().defender());
          },
          error -> System.out.println("createWarListener error: " + error.getMessage())
        );
    }

    public void startWarWith(String enemyID) {
        CreateWarDto warDto = new CreateWarDto(myOwnEmpireID,enemyID,"");
        this.subscriber.subscribe(this.warService.createWar(gameID,warDto),
          result -> warsOnProcess.put(enemyID,result),
          error -> System.out.println("couldn't create war"));
    }

    public void stopWarWith(String enemyID) {
        String warId = warsOnProcess.get(enemyID)._id();
        this.subscriber.subscribe(this.warService.deleteWar(gameID,warId),
          result -> warsOnProcess.remove(enemyID),
          error -> System.out.println("couldn't stop war"));
    }

    public void setContactOverview(ContactsComponent contactsOverviewComponent) {
        this.contactsComponent = contactsOverviewComponent;
    }

    public boolean attacker(String empireID) {
        System.out.println(" wars : " + warsInThisGame.size());
        return warsInThisGame.stream()
          .anyMatch(warDto -> (myOwnEmpireID.equals(warDto.defender()) && empireID.equals(warDto.attacker())));
    }

    public boolean defender(String empireID) {
        System.out.println(" wars : " + warsInThisGame.size());
        return warsInThisGame.stream()
          .anyMatch(warDto -> (empireID.equals(warDto.defender()) && myOwnEmpireID.equals(warDto.attacker())));
    }

    public void addWarInformation(List<WarDto> dto) {
        this.warsInThisGame.addAll(dto);
    }

}