 package de.uniks.stp24;

 import com.fasterxml.jackson.databind.ObjectMapper;
 import de.uniks.stp24.component.BubbleComponent;
 import de.uniks.stp24.component.GangComponent;
 import de.uniks.stp24.controllers.GangCreationController;
 import de.uniks.stp24.model.Gang;
 import de.uniks.stp24.service.SaveLoadService;
 import javafx.collections.FXCollections;
 import javafx.collections.ObservableList;
 import javafx.scene.control.TextArea;
 import javafx.scene.control.TextField;
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
     BubbleComponent bubbleComponent;

     @Spy
     ObjectMapper objectMapper;

     ObservableList<Gang> gangs = FXCollections.observableArrayList();
     String name = "Test Gang";
     Gang gang = new Gang(name, 0, 0, "", "#000000", 0);

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

     @Override
     public void start(Stage stage) throws Exception{
         gangs.add(gang);
         doReturn(gangs).when(saveLoadService).loadGangs();
         super.start(stage);
         this.app.show(this.gangCreationController);
     }

     @Test
     public void testCreatingNewGang() {
         doNothing().when(saveLoadService).saveGang(any());

         ListView<Gang> gangsListView = lookup("#gangsListView").query();

         int gangNums = gangsListView.getItems().size();

         clickOn("#showCreationButton");
         waitForFxEvents();

         clickOn("#gangNameText");
         waitForFxEvents();

         String gangName = "Ashkanian";

         write(gangName);
         waitForFxEvents();

         clickOn("#gangDescriptionText");
         waitForFxEvents();

         String gangDescription = "Ruled by King Ashkan";

         write(gangDescription);
         waitForFxEvents();

         clickOn("#createButton");
         waitForFxEvents();

          assertEquals(gangNums + 1, gangsListView.getItems().size());
          Gang selectedGang = gangsListView.getItems().get(1);
          assertEquals(gangName, selectedGang.name());
          assertEquals(gangDescription, selectedGang.description());
          verify(saveLoadService).saveGang(any());
     }

     @Test
     public void testEditGang() {
         ListView<Gang> gangsListView = lookup("#gangsListView").query();

         clickOn("#showCreationButton");
         waitForFxEvents();

         clickOn("#gangNameText");
         waitForFxEvents();

         String gangName = "Ashkanian";

         write(gangName);
         waitForFxEvents();

         clickOn("#createButton");
         waitForFxEvents();

         Gang selectedGang;
         selectedGang = gangsListView.getItems().get(1);
         assertTrue(selectedGang.description().isEmpty());

         clickOn(gangName);
         waitForFxEvents();

         clickOn("#gangDescriptionText");
         waitForFxEvents();

         String gangDescription = "Ruled by King Ashkan";

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

         selectedGang = gangsListView.getItems().get(1);
         assertEquals(gangName, selectedGang.name());
         assertEquals(gangDescription, selectedGang.description());
         assertEquals(1, selectedGang.flagIndex());
         assertEquals(1, selectedGang.portraitIndex());
         assertEquals(1, selectedGang.colorIndex());

         clickOn(gangName);
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
         selectedGang = gangsListView.getItems().get(1);
         assertEquals(1, selectedGang.flagIndex());
         assertEquals(1, selectedGang.portraitIndex());
         assertEquals(1, selectedGang.colorIndex());

         clickOn("#editButton");
         waitForFxEvents();

         // after edit
         selectedGang = gangsListView.getItems().get(1);
         assertEquals(16, selectedGang.flagIndex());
         assertEquals(16, selectedGang.portraitIndex());
         assertEquals(15, selectedGang.colorIndex());
     }

     @Test
     public void loadingGangsFromSave() {
         ListView<Gang> gangsListView = lookup("#gangsListView").query();
         assertEquals( 1, gangsListView.getItems().size());
         Gang loadedGang = gangsListView.getItems().get(0);
         assertEquals(name, loadedGang.name());
         assertEquals(0, loadedGang.flagIndex());
         assertEquals(0, loadedGang.portraitIndex());
         assertEquals(0, loadedGang.colorIndex());
     }

     @Test
     public void testRandomGenerationAndLocking() {
         ListView<Gang> gangsListView = lookup("#gangsListView").query();

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

         Gang selectedGang = gangsListView.getItems().get(1);
         assertEquals(0, selectedGang.flagIndex());
         assertEquals(0, selectedGang.portraitIndex());
         assertEquals(0, selectedGang.colorIndex());
     }

     @Test
     public void testDeletingGang() {
         ListView<Gang> gangsListView = lookup("#gangsListView").query();

         clickOn("#showCreationButton");
         waitForFxEvents();

         clickOn("#createButton");
         waitForFxEvents();

         int gangNums = gangsListView.getItems().size();

         clickOn("Buccaneers");
         waitForFxEvents();

         clickOn("#showDeletePaneButton");
         waitForFxEvents();

         clickOn("#deleteButton");
         waitForFxEvents();

         assertEquals(gangNums - 1, gangsListView.getItems().size());
     }
 }
