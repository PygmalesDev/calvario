package de.uniks.stp24.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.service.EditAccService;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Map;

@Component(view = "WarningScreen.fxml")
public class WarningScreenComponent extends VBox {

    @FXML
    Text warningText;
    @FXML
    VBox warningContainer;
    @Inject
    App app;
    @Inject
    EditAccService editAccService;
    @Inject
    ObjectMapper objectMapper;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @Inject
    ErrorService errorService;

    @Inject
    public WarningScreenComponent() {
    }

    @OnRender
    public void setBackground(){
        warningContainer.setStyle("-fx-background-color: white;");
    }

    public void cancelDelete() {
        getParent().setVisible(false);
    }

    public void deleteAcc() {
        // delete user and switch back to the login screen
        this.subscriber.subscribe(editAccService.deleteUser(),
            result -> app.show("/login",
                        Map.of("info","deleted"))
            , error -> {
                System.out.println(errorService.getStatus(error));
                // ToDo: error handling and message
                // where should it be shown?
        });
    }

    @OnDestroy
    public void destroy() {
        if(subscriber != null) {
            this.subscriber.dispose();
        }
    }

    public void setWarning(String text) {
        this.warningText.setText(text);
    }
}
