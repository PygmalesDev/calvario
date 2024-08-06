package de.uniks.stp24.ingameTests;

import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.dto.EmpirePrivateDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ContactsServiceTest extends ControllerTest {

    @Spy
    EmpireApiService empireApiService;
    @Spy
    IslandsService islandsService;
    @Spy
    TokenStorage tokenStorage;

    final EmpirePrivateDto emptyPrivateDto = new EmpirePrivateDto(new HashMap<>());
    EmpirePrivateDto empirePrivateDto;
    EmpirePrivateDto falsePrivateDto;
    ReadEmpireDto enemy;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        contactsService.islandsService = islandsService;
        contactsService.empireApiService = empireApiService;
        contactsService.subscriber = new Subscriber();
        contactsService.tokenStorage = tokenStorage;

        enemy = new ReadEmpireDto("today", "today", "empire1", "game1",
          "user1", "brotherhood", "no description", "color", 1, 1, "enemySystem");

        ReadEmpireDto player = new ReadEmpireDto("today", "today", "gameOwner", "game1",
          "player", "boss", "no description", "color", 2, 2, "bossSystem");

        islandsService.saveEmpire("gameOwner", player);
        islandsService.saveEmpire("empire1", enemy);

        doReturn("game1").when(tokenStorage).getGameId();
        doReturn("gameOwner").when(tokenStorage).getEmpireId();
        doReturn(enemy).when(islandsService).getEmpire(any());

        ArrayList<String> listOfIsles = new ArrayList<>();
        listOfIsles.add("isleNr1");
        Object list = new ArrayList<>(listOfIsles);
        Map<String,Object> mapPrivate = new HashMap<>();
        mapPrivate.put("empire1", list);
        Map<String,Object> falsePrivate = new HashMap<>();
        falsePrivate.put("emp",false);
        empirePrivateDto = new EmpirePrivateDto(mapPrivate);
        falsePrivateDto = new EmpirePrivateDto(falsePrivate);

    }

    @Test
    public void addEnemyTest() {
        doReturn(Observable.just(emptyPrivateDto)).when(empireApiService).getPrivate(any(),any());
        doNothing().when(contactsService).saveContacts();
        assertTrue(contactsService.hiddenEmpires.isEmpty());
        assertNull(contactsService.gameID);
        assertNull(contactsService.myOwnEmpireID);
        contactsService.getEmpiresInGame();
        assertTrue(contactsService.seenEnemies.isEmpty());
        assertEquals("gameOwner", contactsService.myOwnEmpireID);
        assertEquals("game1", contactsService.gameID);

        contactsService.addEnemy("empire1","isleNr1");
        assertEquals(1, contactsService.seenEnemies.size());

        contactsService.addEnemy("empire1", "isleNr2");
        assertEquals(1, contactsService.seenEnemies.size());
    }

    @Test
    public void loadDataTest() {
        doReturn(Observable.just(empirePrivateDto)).when(empireApiService).getPrivate(any(),any());
        assertTrue(contactsService.seenEnemies.isEmpty());
        contactsService.getEmpiresInGame();
        sleep(100);
        assertEquals(1, contactsService.seenEnemies.size());
        Contact contact = contactsService.seenEnemies.getFirst();
        assertEquals(1, contact.getDiscoveredIslands().size());
        assertTrue(contact.getDiscoveredIslands().contains("isleNr1"));
        assertFalse(contact.getDiscoveredIslands().contains("isleNr2"));

    }
    @Test
    public void loadFalseDataTest() {
        doReturn(Observable.just(falsePrivateDto)).when(empireApiService).getPrivate(any(),any());

        assertTrue(contactsService.seenEnemies.isEmpty());
        contactsService.getEmpiresInGame();

        sleep(100);
        assertEquals(0, contactsService.seenEnemies.size());
        contactsService.addEnemy("empire1","isleNr1");

    }

    @Test
    public void saveDateTest() {
        doReturn(Observable.just(emptyPrivateDto)).when(empireApiService).getPrivate(any(),any());
        doReturn(Observable.just(empirePrivateDto))
          .when(empireApiService).savePrivate(any(),any(),any(EmpirePrivateDto.class));
        contactsService.getEmpiresInGame();
        contactsService.addEnemy("empire1","isleNr1");

    }

}
