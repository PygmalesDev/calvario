package de.uniks.stp24.component;

import javafx.fxml.FXML;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.fulib.fx.annotation.controller.Component;


import javax.inject.Inject;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;

@Component(view = "CaptainBubble.fxml")
public class BubbleComponent extends Pane {
    @FXML
    Text captainText;
    @FXML
    TextFlow messageBubble;
    Text childText;

    String lastText = "";

    @Inject
    public BubbleComponent() {

    }

    public void setCaptainText(String text) {
        captainText.setText(text);
        lastText = text;
    }

    public void setErrorMode(boolean isError) {
        if (isError){
            setCaptainText("");
            childText.setFill(WHITE);
            messageBubble.setStyle(messageBubble.getStyle() + "-fx-background-color: red;");
        }
        else {
            // setCaptainText(lastText);
            childText.setFill(BLACK);
            messageBubble.setStyle(messageBubble.getStyle() + "-fx-background-color: LIGHTGRAY;");
        }
    }

    public void setBubbleVisible(boolean show) {
        messageBubble.setVisible(show);
    }

    public void addChildren(Text child) {
        child.setFill(WHITE);
        messageBubble.getChildren().add(child);
        childText = child;
    }
}