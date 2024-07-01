package de.uniks.stp24.component.menu;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import javax.inject.Inject;
import static java.lang.Math.ceil;

@Component(view = "CaptainBubble.fxml")
public class BubbleComponent extends Pane {
    @FXML
    public TextArea captainText;
    @FXML
    ImageView captainLogo;
    @FXML
    Pane backgroundBubble;

    ScaleTransition scaleTransition;

    @Inject
    public BubbleComponent() {

    }

    @OnRender
    public void init() {
        scaleTransition = new ScaleTransition(Duration.seconds(0.5), captainLogo);
    }

    public void setCaptainText(String text) {
        captainText.setText(text);
        double calculated = ceil((double) text.length() /22)*37;
        double bubbleOffset, textOffset;

        switch ((int) calculated/37) {
            case 1 -> {
                bubbleOffset = 70;
                textOffset = 0;
            }
            case 2 -> {
                bubbleOffset = 40;
                textOffset = 12;
            }
            case 3 -> {
                bubbleOffset = 10;
                textOffset = 20;
            }
            case 5 -> {
                calculated = 4*37+7;
                bubbleOffset = 0;
                textOffset = 20;
            }
            default -> {
                bubbleOffset = 0;
                textOffset = 20;
            }
        }

        this.backgroundBubble.setPrefHeight(calculated);
        this.backgroundBubble.setLayoutY(bubbleOffset);
        this.captainText.setLayoutY(bubbleOffset+textOffset);
    }

    public void setErrorMode(boolean isError) {
        if (isError) {
            captainText.setStyle("-fx-text-fill: RED");
        }
        else {
            captainText.setStyle("-fx-text-fill: BLACK");
        }
    }

    public void scaleUp() {
        scaleTransition.stop();
        scaleTransition.setToX(1.3);
        scaleTransition.setToY(1.3);
        scaleTransition.play();
    }

    public void scaleDown() {
        scaleTransition.stop();
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();
    }

    @OnDestroy
    public void destroy() {
        captainLogo.setImage(null);
    }
}