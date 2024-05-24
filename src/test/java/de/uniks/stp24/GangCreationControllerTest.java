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

     Gang gang = new Gang("", "", 0, "", 0, "", "#000000", 0);

     @Override
     public void start(Stage stage) throws Exception{
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

         clickOn("#createButton");
         waitForFxEvents();

          assertEquals(gangNums + 1, gangsListView.getItems().size());
     }

     @Test
     public void testRandomGenerationAndLocking() {
         clickOn("#showCreationButton");
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
         clickOn("#lockDescriptionButton");

         clickOn("#randomizeButton");
         waitForFxEvents();


         assertEquals(generatedName, name.getText());
         assertEquals(generatedDescription, description.getText());
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
