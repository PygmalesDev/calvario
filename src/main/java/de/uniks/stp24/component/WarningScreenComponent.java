package de.uniks.stp24.component;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.model.ErrorResponse;
import de.uniks.stp24.service.EditAccService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import retrofit2.HttpException;

import javax.inject.Inject;

@Component(view = "WarningScreen.fxml")
public class WarningScreenComponent extends VBox {

    @FXML
    VBox warningContainer;

    @Inject
    App app;
    @Inject
    EditAccService editAccService;
    @Inject
    ObjectMapper objectMapper;

    @Inject
    public WarningScreenComponent() {
    }


    @OnRender
    public void setBackground(){
        warningContainer.setStyle("-fx-background-color: white;");
    }

    public void cancelDelete(ActionEvent actionEvent) {
        getParent().setVisible(false);
    }

    public void deleteAcc(ActionEvent actionEvent) {
        // delete user and switch back to the login screen
        editAccService.deleteUser().subscribe(result -> {
            app.show("/login");
        }, error ->{
            if(error instanceof HttpException httpError) {
                System.out.println(httpError.code());
                String body = httpError.response().errorBody().string();
                ErrorResponse errorResponse = objectMapper.readValue(body,ErrorResponse.class);
                // ToDo: error handling and message
                //writeText(errorResponse.statusCode());
            }
        });
    }
}
