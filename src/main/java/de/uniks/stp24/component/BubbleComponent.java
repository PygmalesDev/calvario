package de.uniks.stp24.component;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;

@Component(view = "CaptainBubble.fxml")
public class BubbleComponent extends Pane {
    @FXML
    Text captainText;
    @FXML
    ImageView captainLogo;
    @FXML
    TextFlow messageBubble;
    Text childText;
    boolean errorStatus;

    String lastText = "";

    @Inject
    public BubbleComponent() {

    }

    @OnRender
    public void init() {
    }

    public void setCaptainText(String text) {
        captainText.setText(text);
        lastText = text;
    }

    public void setErrorMode(boolean isError) {
        if (isError){
            captainText.setText("");
            childText.setFill(WHITE);
            this.errorStatus = true;
            messageBubble.setStyle(messageBubble.getStyle() + "-fx-background-color: #CF2A27;");
        }
        else {
            if (childText.getText().equals(""))
                captainText.setText(lastText);
            this.errorStatus = false;
            messageBubble.setStyle(messageBubble.getStyle() + "-fx-background-color: #FFD966;");
        }
    }

    public boolean getErrorMode() {
        return this.errorStatus;
    }

    public void setBubbleVisible(boolean show) {
        messageBubble.setVisible(show);
    }

    public void addChildren(Text child) {
        child.setFill(BLACK);
        messageBubble.getChildren().add(child);
        childText = child;
    }

    @OnDestroy
    public void destroy() {
        captainLogo.setImage(null);
    }
}