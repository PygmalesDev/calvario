package de.uniks.stp24.component;

import de.uniks.stp24.App;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.menu.TimerService;
import org.fulib.fx.annotation.controller.Component;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

@Component(view = "Clock.fxml")
public class ClockComponent extends AnchorPane {

    @FXML
    ToggleButton flagToggle;
    @FXML
    ImageView randomEventImage;
    @FXML
    Label remainingSeasonsLabel;
    @FXML
    AnchorPane anchor;
    @FXML
    Button x1Button;
    @FXML
    Button x2Button;
    @FXML
    Button x3Button;
    @FXML
    ToggleButton pauseClockButton;
    @FXML
    Label seasonLabel;
    @FXML
    Label countdownLabel;

    @Inject
    App app;
    @Inject
    TimerService timerService;
    @Inject
    InGameService inGameService;

    @Inject
    public ClockComponent() {

    }

    @OnInit
    public void onInit() {

        PropertyChangeListener callHandleTimeChange = this::timeChange;
        timerService.listeners().addPropertyChangeListener("countdown", callHandleTimeChange);
    }

    @OnRender
    public void onRender() {

        String css = Objects.requireNonNull(this.getClass().getResource("/de/uniks/stp24/clock.css")).toExternalForm();
        this.getStylesheets().add(css);

        timerService.start();
        seasonLabel.setText("1");
        countdownLabel.setText(translateCountdown(timerService.getCountdown()));
    }

    public void turnFlags() {
    }

    private void timeChange(@NotNull PropertyChangeEvent change) {
        int countdown = (int) change.getNewValue();
        countdownLabel.setText(translateCountdown(countdown));
    }

    private String translateCountdown(int countdown) {
        String suffix = (countdown % 60) < 10 ? "0" : "";
        return (countdown / 60) + ":" + suffix + (countdown % 60);
    }

    @OnDestroy
    public void onDestroy() {
        timerService.listeners().removePropertyChangeListener("countdown", this::timeChange);
        timerService.stop();
    }

    public void pauseClock() {
        if (pauseClockButton.isSelected()) {
            timerService.stop();
        } else {
            timerService.resume();
        }
    }

    public void x3() {
        timerService.setSpeed(3);
    }

    public void x2() {
        timerService.setSpeed(2);
    }

    public void x1() {
        timerService.setSpeed(1);
    }
}
