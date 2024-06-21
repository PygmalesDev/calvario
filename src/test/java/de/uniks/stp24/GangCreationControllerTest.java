 package de.uniks.stp24;

 import com.fasterxml.jackson.databind.ObjectMapper;
 import de.uniks.stp24.component.menu.GangComponent;
 import de.uniks.stp24.component.menu.GangDeletionComponent;
 import de.uniks.stp24.controllers.GangCreationController;
 import de.uniks.stp24.model.Gang;
 import de.uniks.stp24.model.GangElement;
 import de.uniks.stp24.rest.GameMembersApiService;
 import de.uniks.stp24.service.ImageCache;
 import de.uniks.stp24.service.SaveLoadService;
 import de.uniks.stp24.service.TokenStorage;
 import de.uniks.stp24.service.menu.LobbyService;
 import javafx.application.Platform;
 import javafx.collections.FXCollections;
 import javafx.collections.ObservableList;
 import javafx.scene.control.TextArea;
 import javafx.scene.control.TextField;
 import javafx.scene.input.MouseButton;
 import org.fulib.fx.controller.Subscriber;
 import org.junit.jupiter.api.BeforeEach;
 import org.mockito.InjectMocks;
 import org.mockito.Spy;
 import javafx.stage.Stage;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.Mock;
 import org.mockito.junit.jupiter.MockitoExtension;
 import javafx.scene.control.ListView;

 import javax.inject.Provider;

 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.Mockito.*;
 import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

 @ExtendWith(MockitoExtension.class)
 public class GangCreationControllerTest extends ControllerTest {

     @Spy
     ObjectMapper objectMapper;

     @Spy
     ImageCache imageCache;

     @Spy
     TokenStorage tokenStorage;

     @Spy
     Subscriber subscriber;

     @Spy
     GameMembersApiService gameMembersApiService;

     @Spy
     LobbyService lobbyService;

     ObservableList<Gang> gangs = FXCollections.observableArrayList();
     Gang gang = new Gang("Test Gang", 0, 0, "", "#000000", 0);
     ListView<GangElement> gangsListView;

     @Mock
     SaveLoadService saveLoadService;

     @Spy
     Provider<GangComponent> gangComponentProvider = new Provider(){
         @Override
         public GangComponent get() {
             return new GangComponent();
         }
     };

     @InjectMocks
     GangCreationController gangCreationController;
     @InjectMocks
     GangDeletionComponent gangDeletionComponent;

     @Override
     public void start(Stage stage) throws Exception{
         super.start(stage);
         this.gangCreationController.gangDeletionComponent = this.gangDeletionComponent;
         gangs.add(gang);
         doReturn(gangs).when(saveLoadService).loadGangs();

         app.show(this.gangCreationController);
     }

     @BeforeEach
     public void initListView() {
         gangsListView = lookup("#gangsListView").query();
     }

     @Test
     public void testCreatingNewGang() {
         doNothing().when(saveLoadService).saveGang(any());

         int gangNums = gangsListView.getItems().size();

         clickOn("#showCreationButton");
         waitForFxEvents();

         String gangName = "Ashkanian";

         clickOn("#gangNameText");
         waitForFxEvents();

         release(MouseButton.PRIMARY);
         waitForFxEvents();

         write(gangName);
         waitForFxEvents();

         String gangDescription = "Ruled by King Ashkan";

         clickOn("#gangDescriptionText");
         waitForFxEvents();

         release(MouseButton.PRIMARY);
         waitForFxEvents();

         write(gangDescription);
         waitForFxEvents();

         clickOn("#createButton");
         waitForFxEvents();

          assertEquals(gangNums + 1, gangsListView.getItems().size());
          GangElement selectedGang = gangsListView.getItems().get(1);
          assertEquals(gangName, selectedGang.gang().name());
          assertEquals(gangDescription, selectedGang.gang().description());
          verify(saveLoadService).saveGang(any());
     }

     @Test
     public void testEditGang() {
         waitForFxEvents();

         clickOn("Test Gang");
         waitForFxEvents();

         clickOn("#gangNameText");
         waitForFxEvents();
         Platform.runLater(() -> {((TextField) lookup("#gangNameText").query()).clear();});

         waitForFxEvents();
         write("Ashkanian");
         waitForFxEvents();

         clickOn("#editButton");
         waitForFxEvents();

         GangElement selectedGang;
         selectedGang = gangsListView.getItems().get(0);
         assertEquals("Ashkanian", selectedGang.gang().name());
         assertTrue(selectedGang.gang().description().isEmpty());

         clickOn("Ashkanian");
         waitForFxEvents();

         String gangDescription = "Ruled by King Ashkan";

         clickOn("#gangDescriptionText");
         waitForFxEvents();

         waitForFxEvents();

         write(gangDescription);
         waitForFxEvents();

         clickOn("#nextPortraitButton");
         waitForFxEvents();

         clickOn("#nextFlagButton");
         waitForFxEvents();

         clickOn("#nextColorButton");
         waitForFxEvents();

         clickOn("#editButton");
         waitForFxEvents();

         selectedGang = gangsListView.getItems().get(0);
         assertEquals("Ashkanian", selectedGang.gang().name());
         assertEquals(gangDescription, selectedGang.gang().description());
         assertEquals(1, selectedGang.gang().flagIndex());
         assertEquals(1, selectedGang.gang().portraitIndex());
         assertEquals(1, selectedGang.gang().colorIndex());

         clickOn("Ashkanian");
         waitForFxEvents();

         clickOn("#lastFlagButton");
         waitForFxEvents();

         clickOn("#lastFlagButton");
         waitForFxEvents();

         clickOn("#lastPortraitButton");
         waitForFxEvents();

         clickOn("#lastPortraitButton");
         waitForFxEvents();

         clickOn("#lastColorButton");
         waitForFxEvents();

         clickOn("#lastColorButton");
         waitForFxEvents();

         // before edit
         selectedGang = gangsListView.getItems().get(0);
         assertEquals(1, selectedGang.gang().flagIndex());
         assertEquals(1, selectedGang.gang().portraitIndex());
         assertEquals(1, selectedGang.gang().colorIndex());

         clickOn("#editButton");
         waitForFxEvents();

         // after edit
         selectedGang = gangsListView.getItems().get(0);
         assertEquals(16, selectedGang.gang().flagIndex());
         assertEquals(16, selectedGang.gang().portraitIndex());
         assertEquals(15, selectedGang.gang().colorIndex());
     }

     @Test
     public void loadingGangsFromSave() {
         assertEquals( 1, gangsListView.getItems().size());
         GangElement loadedGang = gangsListView.getItems().get(0);
         assertEquals("Test Gang", loadedGang.gang().name());
         assertEquals(0, loadedGang.gang().flagIndex());
         assertEquals(0, loadedGang.gang().portraitIndex());
         assertEquals(0, loadedGang.gang().colorIndex());
     }

     @Test
     public void testRandomGenerationAndLocking() {
         clickOn("#showCreationButton");
         waitForFxEvents();

         clickOn("#lockColorButton");
         waitForFxEvents();

         clickOn("#lockPortraitButton");
         waitForFxEvents();

         clickOn("#lockFlagButton");
         waitForFxEvents();

         TextField name = lookup("#gangNameText").query();
         assertEquals("", name.getText());

         TextArea description = lookup("#gangDescriptionText").query();
         assertEquals("", description.getText());

         clickOn("#randomizeButton");
         waitForFxEvents();

         String generatedName = name.getText();
         String generatedDescription = description.getText();

         assertNotNull(generatedName);
         assertNotNull(generatedDescription);

         clickOn("#lockNameButton");
         waitForFxEvents();

         clickOn("#lockDescriptionButton");
         waitForFxEvents();

         clickOn("#randomizeButton");
         waitForFxEvents();

         assertEquals(generatedName, name.getText());
         assertEquals(generatedDescription, description.getText());

         clickOn("#createButton");
         waitForFxEvents();

         GangElement selectedGang = gangsListView.getItems().get(1);
         assertEquals(0, selectedGang.gang().flagIndex());
         assertEquals(0, selectedGang.gang().portraitIndex());
         assertEquals(0, selectedGang.gang().colorIndex());
     }

     @Test
     public void testDeletingGang() {
         clickOn("#showCreationButton");
         waitForFxEvents();

         clickOn("#createButton");
         waitForFxEvents();

         int gangNums = gangsListView.getItems().size();

         clickOn("Buccaneers");
         waitForFxEvents();

         clickOn("#showDeletePaneButton");
         waitForFxEvents();

         clickOn("#deleteGangButton");
         waitForFxEvents();

         assertEquals(gangNums - 1, gangsListView.getItems().size());
     }

//     @Test
//     public void goingBackToLobbyNoGang() {
//         doReturn(null).when(this.app).show(eq("/lobby"), any());
//         doReturn(Observable.just(new MemberDto(false,"1", null, "1"))).when(lobbyService).getMember(any(), any());
//         doReturn(Observable.just(new MemberDto(false, "1", null, "1"))).when(lobbyService).updateMember(null, "1", false, null);
//
//         clickOn("#backButton");
//         waitForFxEvents();
//
//         verify(lobbyService).updateMember(null, "1", false, null);
//     }
 }
