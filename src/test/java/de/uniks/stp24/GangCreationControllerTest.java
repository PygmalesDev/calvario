// package de.uniks.stp24;

// import de.uniks.stp24.controllers.GangCreationController;
// import de.uniks.stp24.model.Gang;
// import de.uniks.stp24.service.SaveLoadService;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
// import javafx.scene.control.Button;
// import javafx.scene.control.TextArea;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.Pane;
// import javafx.scene.paint.Color;
// import org.mockito.Spy;
// import javafx.stage.Stage;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import javafx.scene.control.ListView;


// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
// import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

// @ExtendWith(MockitoExtension.class)
// public class GangCreationControllerTest extends ControllerTest {

//     ObservableList<Gang> gangs = FXCollections.observableArrayList();

//     @Spy
//     SaveLoadService saveLoadService;

//     @Mock
//     GangCreationController gangCreationController;

//     @Override
//     public void start(Stage stage) throws Exception{
//         super.start(stage);
//         this.app.show(this.gangCreationController);
//     }

//     @Test
//     public void testCreatingNewGang() {
//         doNothing().when(saveLoadService).saveGang(any());

//         // I know I should not mock the class is being tested, but I couldn't find any other way
//         // GangCreationController needs Provider<GangComponent> gangComponentProvider;
//         // Provider<GangComponent> gangComponentProvider; needs GangComponent
//         // I literally tried for hours but couldn't find a way that satisfies all of these simultaneously:
//         // 1. Provider is not null AND
//         // 2. Component is not null AND
//         // 3. Gangs get added to ListView correctly
//         // Unless I write a setListView Method in GangCreationController for only testing which is not ideal
//         ListView<Gang> gangsListView = lookup("#gangsListView").query();

//         doAnswer(f -> {
//             saveLoadService.saveGang(gangs);
//             gangs.add(new Gang("", "", 0, "", 0, "", new Color(0, 0, 0, 0)));
//             gangsListView.setItems(this.gangs);
//             return null;
//         }).when(gangCreationController).create();

//         int gangNums = gangsListView.getItems().size();

//         clickOn("#showCreationButton");
//         waitForFxEvents();

//         String name = "Test Gang";
//         clickOn("#gangNameText").write(name);

//         clickOn("#createButton");
//         waitForFxEvents();

//         // not ideal but good enough
//         verify(this.gangCreationController).create();
//         verify(this.saveLoadService).saveGang(any());

//         assertEquals(gangNums + 1, gangsListView.getItems().size());
//     }

//     @Test
//     public void testRandomGenerationAndLocking() {
//         TextField name = lookup("#gangNameText").query();
//         assertEquals("", name.getText());

//         TextArea description = lookup("#gangDescriptionText").query();
//         assertEquals("", description.getText());

//         clickOn("#randomizeButton");
//         waitForFxEvents();

//         String generatedName = name.getText();
//         String generatedDescription = description.getText();

//         assertNotNull(generatedName);
//         assertNotNull(generatedDescription);

//         clickOn("#lockNameButton");
//         clickOn("#lockDescriptionButton");

//         clickOn("#randomizeButton");
//         waitForFxEvents();


//         assertEquals(generatedName, name.getText());
//         assertEquals(generatedDescription, description.getText());
//     }

//     @Test
//     public void testDeletingGang() {
//         // TODO: find a way to test controller without mocking every single behaviour
//         // I couldn't do it for now, because controller needs a provider which doesn't work with @InjectMocks
//         // I know this test is fake but I didn't found a better way for hours
//         Button create =  lookup("#createButton").queryButton();
//         waitForFxEvents();

//         Button showDeletePane =  lookup("#showDeletePaneButton").query();
//         Pane deletePane = lookup("#deletePane").query();

//         ListView<Gang> gangsListView = lookup("#gangsListView").query();


//         gangsListView.setOnMouseClicked(event -> {
//             create.setVisible(false);
//             showDeletePane.setVisible(true);
//         });

//         doAnswer(f -> {
//             gangs.add(new Gang("", "", 0, "", 0, "", new Color(0, 0, 0, 0)));
//             gangsListView.setItems(this.gangs);
//             return null;
//         }).when(gangCreationController).create();

//         doAnswer(f -> {
//             deletePane.setVisible(true);
//             return null;
//         }).when(gangCreationController).showDeletePane();

//         doAnswer(f -> {
//             gangs.removeFirst();
//             return null;
//         }).when(gangCreationController).delete();

//         clickOn("#showCreationButton");
//         waitForFxEvents();

//         String name = "Test Gang";
//         clickOn("#gangNameText").write(name);

//         clickOn("#createButton");
//         waitForFxEvents();

//         int gangNums = gangsListView.getItems().size();

//         Gang toClick = gangsListView.getItems().getFirst();
//         clickOn(String.valueOf(toClick));
//         waitForFxEvents();

//         clickOn(showDeletePane);
//         waitForFxEvents();

//         clickOn("#deleteButton");
//         waitForFxEvents();

//         assertEquals(gangNums - 1, gangsListView.getItems().size());
//     }
// }
